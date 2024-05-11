package graph

import common.Identifiable

abstract class Graph<T>(
    val nodes: List<Node>,
    val edges: List<Edge<T>>
) : Identifiable() {
    abstract fun calculateTotalLengthOf(path: Array<Edge<T>>): T

    fun getRandomPath(startNode: Node = nodes.random()): MutableList<Edge<T>> {
        val visited = mutableListOf<Edge<T>>()
        val path = mutableListOf<Edge<T>>()

        fun dfs(node: Node) {
            val suitableEdges = edges.filter { it.source == node }
            val notVisitedSuitableEdges = suitableEdges.filter { !visited.contains(it) }

            if (suitableEdges.isEmpty()) {
                throw IllegalArgumentException("There are no output edges from the node with id ${node.id}")
            }

            if (visited.size >= edges.size && node.id == startNode.id) return

            val nextEdge = (notVisitedSuitableEdges.ifEmpty { suitableEdges }).random()

            visited.add(nextEdge)
            path.add(nextEdge)
            dfs(nextEdge.destination)
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