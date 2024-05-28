import graph.Edge
import graph.EdgeType
import graph.Graph
import graph.Node
import utils.constants.NOT_EXISTENT_PATH_VALUE

/**
 * Граф, в котором веса рёбер являются вещественными значениями
 * @see Graph
 */
class DoubleGraph(nodes: List<Node>, edges: MutableList<Edge<Double>>): Graph<Double, Edge<Double>>(nodes, edges) {
    override fun calculateTotalLengthOf(path: List<Edge<Double>>): Double {
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