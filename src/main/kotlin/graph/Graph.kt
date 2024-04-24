package graph

import common.Identifiable

abstract class Graph<T>(
    _nodes: List<Node>,
    _edges: List<Edge<T>>
) : Identifiable() {
    val nodes: List<Node> = _nodes
    var edges: List<Edge<T>>

    init {
        val result = mutableListOf<Edge<T>>()

        for (edge in _edges) {
            if (edge.type == EdgeType.NOT_ORIENTED) {
                result.add(Edge(edge.destination, edge.source, edge.weight, EdgeType.DIRECTED))
            }
            result.add(edge.copy(type = EdgeType.DIRECTED))
        }

        edges = result
    }

    abstract fun calculateTotalLengthOf(path: Array<Edge<T>>): T

    fun getRandomPath(startNode: Node = nodes.random()): MutableList<Edge<T>> {
        val visited = mutableListOf<Edge<T>>()
        val path = mutableListOf<Edge<T>>()

        fun dfs(node: Node) {
            val suitableEdges = edges.filter { it.source == node }
            if (suitableEdges.all { visited.any { edge -> edge.id == it.id } }) {
                path.removeLastOrNull()
                visited.removeLastOrNull()
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

        dfs(startNode)
        val backToStartPath = getPathBetween(path.last().destination, startNode)
        backToStartPath?.let { edges -> path.addAll(edges) }
        return path
    }

    private fun getPathBetween(startNode: Node, endNode: Node): List<Edge<T>>? {
        val visitedEdges = mutableSetOf<Node>()
        val queue = ArrayDeque<List<Edge<T>>>()
        queue.add(listOf())

        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val currentNode = if (path.isNotEmpty()) path.last().destination else startNode

            if (currentNode == endNode) {
                return path
            }

            if (currentNode !in visitedEdges) {
                visitedEdges.add(currentNode)
                val adjacentEdges = edges.filter { it.source == currentNode }
                for (edge in adjacentEdges) {
                    queue.add(path + edge)
                }
            }
        }

        return null
    }
}