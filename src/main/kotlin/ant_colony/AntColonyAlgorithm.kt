package ant_colony

import graph.Edge
import graph.Graph
import graph.Node
import utils.helpers.LoggingHelper

class PheromoneDoubleGraph(
    nodes: List<Node>,
    edges: MutableList<PheromoneEdge<Double>>
) : PheromoneGraph<Double>(nodes, edges) {
    override fun calculateTotalLengthOf(path: Array<Edge<Double>>): Double {
        return path.sumOf { it.weight }
    }
}

class AntColonyAlgorithm {
    private val logger = LoggingHelper().getLogger(AntColonyAlgorithm::class.java.simpleName)

    fun start(
        graph: Graph<Double, Edge<Double>>,
        configuration: AntColonyAlgorithmConfiguration
    ): MutableList<PheromoneEdge<Double>> {
        // определяем начальные значения параметров
        val (
            iterationCount, antCount, startPheromoneValue, proximityCoefficient, alpha, beta, remainingPheromoneRate, q, startNodeId
        ) = configuration
        // определяем стартовую вершину
        val startNode = if (startNodeId == null) {
            graph.nodes.random()
        } else {
            graph.getNodeById(startNodeId) ?: graph.nodes.random()
        }

        logger.info { "Adding pheromones on the edge of the graph (initial value: ${startPheromoneValue})" }
        // преобразуем исходный граф, полученный от пользователя в граф, содержащий феромоны
        val phGraph = PheromoneDoubleGraph(
            graph.nodes,
            graph.edges.map { PheromoneEdge(it.id, it.source, it.destination, it.weight, it.type, startPheromoneValue) }.toMutableList()
        )

        // создаём переменные для хранения лучшего (кратчайшего) пути и его длины
        var bestPath = mutableListOf<PheromoneEdge<Double>>()
        var bestLength = Double.MAX_VALUE

        logger.info { "An algorithm for simulating an ant colony is launched ($iterationCount iterations, $antCount ants)" }
        // пока не будет достигнуто максимальное количество итераций
        for (iteration in 1..iterationCount) {
            // феромоны на путях испаряются
            phGraph.edges.forEach { it.pheromoneCount *= remainingPheromoneRate }

            // для каждого муравья
            for (antIndex in 1..antCount) {
                val ant = Ant("Ant-${antIndex}")
                // получаем путь, по которому прошёл муравей
                val path = ant.getPath(phGraph, startNode, proximityCoefficient, alpha, beta)
                // вычисляем его длину
                val length = phGraph.calculateTotalLengthOf(path.toTypedArray())

                // сравниваем с лучшим путём и при необходимости обновляем его
                if (length < bestLength) {
                    logger.info { "The minimum path has been updated!\tIteration number: $iteration, length: $length" }
                    bestLength = length
                    bestPath = path
                }

                // обновляем количество феромонов на пройденный ребёр (добавка)
                path.forEach { edge ->
                    val indexInGraph = phGraph.edges.indexOfFirst { it.id == edge.id }
                    val newPheromoneCount = q / length
                    phGraph.edges[indexInGraph].pheromoneCount += newPheromoneCount
                }
            }
        }

        return bestPath
    }

    private fun <T> Graph<T, Edge<T>>.getNodeById(id: String): Node? {
        return nodes.find { it.id == id }
    }
}