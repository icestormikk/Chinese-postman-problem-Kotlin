package ant_colony

import graph.Edge
import graph.Graph
import graph.Node
import utils.helpers.LoggingHelper

class PheromoneDoubleGraph(
    nodes: List<Node>,
    edges: List<PheromoneEdge<Double>>
) : PheromoneGraph<Double>(nodes, edges) {
    override fun calculateTotalLengthOf(path: Array<Edge<Double>>): Double {
        return path.sumOf { it.weight }
    }
}

class AntColonyAlgorithm {
    private val logger = LoggingHelper().getLogger(AntColonyAlgorithm::class.java.simpleName)

    fun start(
        graph: Graph<Double>,
        configuration: AntColonyAlgorithmConfiguration
    ): MutableList<PheromoneEdge<Double>> {
        val (
            iterationCount, antCount, startPheromoneValue, proximityCoefficient, alpha, beta, remainingPheromoneRate, q, startNodeId
        ) = configuration
        val startNode = if (startNodeId == null) {
            graph.nodes.random()
        } else {
            graph.getNodeById(startNodeId) ?: graph.nodes.random()
        }

        logger.info { "Adding pheromones on the edge of the graph (initial value: ${startPheromoneValue})" }
        val phGraph = PheromoneDoubleGraph(
            graph.nodes,
            graph.edges.map { PheromoneEdge(it.id, it.source, it.destination, it.weight, it.type, startPheromoneValue) }
        )

        var bestPath = mutableListOf<PheromoneEdge<Double>>()
        var bestLength = Double.MAX_VALUE

        logger.info { "An algorithm for simulating an ant colony is launched ($iterationCount iterations, $antCount ants)" }
        for (iteration in 1..iterationCount) {
            phGraph.edges.forEach { it.pheromoneCount * remainingPheromoneRate }

            for (antIndex in 1..antCount) {
                logger.info { "Iteration: $iteration, Ant: ${antIndex}" }
                val ant = Ant("Ant-${antIndex}")
                val path = ant.getPath(phGraph, startNode, proximityCoefficient, alpha, beta)
                val length = phGraph.calculateTotalLengthOf(path.toTypedArray())

                if (length < bestLength) {
                    logger.info { "The minimum path has been updated!\tIteration number: $iteration, length: $length" }
                    bestLength = length
                    bestPath = path
                }

                path.forEach { edge ->
                    val indexInGraph = phGraph.edges.indexOfFirst { it.id == edge.id }
                    val newPheromoneCount = q / length
                    phGraph.edges[indexInGraph].pheromoneCount += newPheromoneCount
                }
            }
        }

        return bestPath
    }

    private fun <T> Graph<T>.getNodeById(id: String): Node? {
        return nodes.find { it.id == id }
    }
}