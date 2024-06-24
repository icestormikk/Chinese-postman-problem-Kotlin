package ant_colony

import graph.Edge
import graph.EdgeType
import graph.Graph
import graph.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import utils.constants.NOT_EXISTENT_PATH_VALUE
import utils.helpers.LoggingHelper

class PheromoneDoubleGraph(
    nodes: List<Node>,
    edges: MutableList<PheromoneEdge<Double>>
) : PheromoneGraph<Double>(nodes, edges) {
    override fun calculateTotalLengthOf(path: List<PheromoneEdge<Double>>): Double {
        for (i in 1..<path.size) {
            val previousEdge = path[i - 1]
            val edge = path[i]

            val previousEdgeNodes = arrayOf(previousEdge.source, previousEdge.destination)
            val currentEdgeNodes = arrayOf(edge.source, edge.destination)

            when (edge.type) {
                EdgeType.NOT_ORIENTED -> {
                    when (previousEdge.type) {
                        EdgeType.NOT_ORIENTED -> {
                            if (currentEdgeNodes.none { node -> previousEdgeNodes.contains(node) }) {
                                return NOT_EXISTENT_PATH_VALUE
                            }
                        }
                        EdgeType.DIRECTED -> {
                            if (!currentEdgeNodes.contains(previousEdge.destination)) {
                                return NOT_EXISTENT_PATH_VALUE
                            }
                        }
                    }
                }
                EdgeType.DIRECTED -> {
                    when (previousEdge.type) {
                        EdgeType.NOT_ORIENTED -> {
                            if (!previousEdgeNodes.contains(edge.source)) {
                                return NOT_EXISTENT_PATH_VALUE
                            }
                        }
                        EdgeType.DIRECTED -> {
                            if (edge.source.id != previousEdge.destination.id) {
                                return NOT_EXISTENT_PATH_VALUE
                            }
                        }
                    }
                }
            }
        }

        return path.sumOf { it.weight }
    }
}


class AntColonyAlgorithm {
    private val logger = LoggingHelper().getLogger(AntColonyAlgorithm::class.java.simpleName)

    companion object {
        private const val MAX_TRY_COUNT = 10
    }

    suspend fun start(
        graph: Graph<Double, Edge<Double>>,
        configuration: AntColonyAlgorithmConfiguration
    ): MutableList<PheromoneEdge<Double>> {
        // определяем начальные значения параметров
        val (
            iterationCount, antCount, startPheromoneValue, proximityCoefficient, alpha, beta, remainingPheromoneRate, q
        ) = configuration

        logger.info { "Adding pheromones on the edge of the graph (initial value: ${startPheromoneValue})" }
        // преобразуем исходный граф, полученный от пользователя в граф, содержащий феромоны
        val phGraph = PheromoneDoubleGraph(
            graph.nodes,
            graph.edges.map { PheromoneEdge(it.id, it.source, it.destination, it.weight, it.type, startPheromoneValue) }.toMutableList()
        )

        // создаём переменные для хранения лучшего (кратчайшего) пути и его длины
        var bestPath = mutableListOf<PheromoneEdge<Double>>()
        var bestLength = Double.MAX_VALUE

        @Synchronized fun updateBestPath(path: MutableList<PheromoneEdge<Double>>, length: Double, iteration: Int) {
            if (length < bestLength) {
                logger.info { "The minimum path has been updated!\tIteration number: $iteration\tLength: $length" }
                bestLength = length
                bestPath = path
            }
        }
        @Synchronized fun updatePheromones(path: MutableList<PheromoneEdge<Double>>, length: Double) {
            path.forEach { edge ->
                val indexInGraph = phGraph.edges.indexOfFirst { it.id == edge.id }
                val newPheromoneCount = q / length
                phGraph.edges[indexInGraph].pheromoneCount += newPheromoneCount
            }
        }

        logger.info { "An algorithm for simulating an ant colony is launched ($iterationCount iterations, $antCount ants)" }
        // пока не будет достигнуто максимальное количество итераций
        for (iteration in 1..iterationCount) {
            if (iteration % 10 == 0) {
                logger.info { "Passed #$iteration iteration" }
            }

            // феромоны на путях испаряются
            phGraph.edges.forEach { it.pheromoneCount *= remainingPheromoneRate }

            // вычисляем количество муравьёв для каждой из корутин (для каждого потока)
            val antsByCoroutine = when (antCount) {
                in 0..100 -> antCount / 10
                in 101..1000 -> antCount / 100
                else -> antCount / 500
            }

            // запускаем поток для каждой из подгрупп муравьёв
            val jobs = List (antCount / antsByCoroutine) {
                CoroutineScope(Dispatchers.Default).launch {
                    // для каждого муравья
                    for (i in 1..antsByCoroutine) {
                        val ant = Ant("Ant-${i}")
                        // получаем путь, по которому прошёл муравей
                        var path: MutableList<PheromoneEdge<Double>>? = null

                        for (j in 1..MAX_TRY_COUNT) {
                            val startNode = graph.nodes.random()
                            path = ant.getPath(phGraph, startNode, proximityCoefficient, alpha, beta, bestLength)
                            if (path != null) break
                        }

                        if (path == null) break

                        // вычисляем его длину
                        val length = phGraph.calculateTotalLengthOf(path)
                        // сравниваем с лучшим путём и при необходимости обновляем его
                        updateBestPath(path, length, iteration)
                        // обновляем количество феромонов на пройденный ребёр (добавка)
                        updatePheromones(path, length)
                    }
                }
            }
            jobs.joinAll()
        }

        return bestPath
    }
}