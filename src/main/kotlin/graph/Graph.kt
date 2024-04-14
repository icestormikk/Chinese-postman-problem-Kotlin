package graph

import common.Identifiable

abstract class Graph<T>(
    private val _nodes: List<Node>,
    private val _edges: List<Edge<T>>
) : Identifiable() {
    val nodes: List<Node> = _nodes
    val edges: List<Edge<T>> by lazy { initializeEdges() }

    private fun initializeEdges(): MutableList<Edge<T>> {
        val result = mutableListOf<Edge<T>>()

        for (edge in _edges) {
            if (edge.type == EdgeType.NOT_ORIENTED) {
                result.add(Edge(edge.destination, edge.source, edge.weight, EdgeType.DIRECTED))
            }
            result.add(edge.copy(type = EdgeType.DIRECTED))
        }

        return result
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