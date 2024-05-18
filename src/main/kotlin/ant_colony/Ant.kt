package ant_colony

import graph.Node
import kotlin.math.pow

data class Ant(val id: String) {
    fun getPath(
        graph: PheromoneGraph<Double>,
        startNode: Node,
        proximityCoefficient: Double,
        alpha: Double,
        beta: Double
    ): MutableList<PheromoneEdge<Double>> {
        val visitedEdgeIds = mutableSetOf<String>()

        fun getNextEdge(currentNode: Node): PheromoneEdge<Double> {
            val suitableEdges = graph.edges.filter { it.source.id == currentNode.id }

            val totalDesire = suitableEdges.sumOf { it.pheromoneCount.pow(alpha) * (proximityCoefficient / it.weight).pow(beta) }

            val randomValue = Math.random()
            var sum = 0.0
            for (edge in suitableEdges) {
                sum += (edge.pheromoneCount.pow(alpha) * (proximityCoefficient / edge.weight).pow(beta)) / totalDesire
                if (randomValue < sum) {
                    return edge
                }
            }

            throw IllegalStateException("Suitable edges were not found")
        }

        val path = mutableListOf<PheromoneEdge<Double>>()
        var currentNode = startNode
        while (!(visitedEdgeIds.size >= graph.edges.size && currentNode.id == startNode.id)) {
            val nextEdge = getNextEdge(currentNode)
            visitedEdgeIds.add(nextEdge.id)
            path.add(nextEdge)
            currentNode = nextEdge.destination
        }

        return path
    }
}
