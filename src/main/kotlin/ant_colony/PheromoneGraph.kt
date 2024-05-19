package ant_colony

import graph.EdgeType
import graph.Graph
import graph.Node

abstract class PheromoneGraph<T>(nodes: List<Node>, override val edges: List<PheromoneEdge<T>>): Graph<T>(nodes, edges) {
    override fun getEdgesFrom(node: Node): List<PheromoneEdge<T>> {
        return edges.filter {
            (it.type == EdgeType.DIRECTED && it.source.id == node.id) ||
                    (it.type == EdgeType.NOT_ORIENTED && (it.source.id == node.id || it.destination.id == node.id))
        }
    }
}