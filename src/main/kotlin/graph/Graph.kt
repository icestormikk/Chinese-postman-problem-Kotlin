package graph

import common.Identifiable

abstract class Graph<T>(
    val nodes: List<Node>,
    var edges: MutableList<Edge<T>>
) : Identifiable() {

    init {
        val result = mutableListOf<Edge<T>>()

        for (edge in edges) {
            if (edge.type == EdgeType.NOT_ORIENTED) {
                result.add(Edge(edge.destination, edge.source, edge.weight, EdgeType.DIRECTED))
            }
            result.add(edge.copy(type = EdgeType.DIRECTED))
        }

        edges = result
    }

    abstract fun calculateTotalLengthOf(path: List<Edge<T>>): T

    fun getRandomPath(startNode: Node = nodes.random()): MutableList<Edge<T>> {
        val visited = mutableListOf<Edge<T>>()
        val path = mutableListOf<Edge<T>>()

        fun dfs(node: Node) {
            val suitableEdges = edges.filter { it.source == node }
            if (suitableEdges.all { visited.any { edge -> edge.id == it.id } }) {
                path.removeLast()
                visited.removeLast()
                return
            }

            for (edge in suitableEdges.shuffled()) {
                if (visited.none { it.id == edge.id }) {
                    visited.add(edge)
                    path.add(edge)
                    dfs(edge.destination)
                }
            }
        }

        try {
            dfs(startNode)

            val finalEdge = edges.find { it.destination.id == startNode.id }
                ?: throw Error("Final edge undefined")
            return path.plus(finalEdge).toMutableList()
        } catch (ex: Exception) {
            throw Error("Error while finding a path: " + ex.message)
        }
    }
}