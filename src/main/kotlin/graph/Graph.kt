package graph

import common.Identifiable

abstract class Graph<T>(
    open val nodes: List<Node>,
    open val edges: List<Edge<T>>
) : Identifiable() {
    private val adjacencyList: MutableMap<String, MutableList<Edge<T>>> = mutableMapOf()

    init {
        val addOrAppend = fun(edge: Edge<T>) {
            if (adjacencyList[edge.source.id.toString()] != null) {
                val isAlreadyDefined = adjacencyList[edge.source.id.toString()]!!.find {
                    it.destination.id == edge.destination.id
                } != null
                if (isAlreadyDefined) throw Error("Duplicated edge found")
                adjacencyList[edge.source.id.toString()]?.add(edge)
            } else {
                adjacencyList[edge.source.id.toString()] = mutableListOf(edge)
            }
        }

        for (edge in edges) {
            val directedEdge = edge.copy(type = EdgeType.DIRECTED)
            addOrAppend(directedEdge)

            if (edge.type == EdgeType.NOT_ORIENTED) {
                val reversedEdge = Edge(edge.destination, edge.source, edge.weight, EdgeType.DIRECTED)
                addOrAppend(reversedEdge)
            }
        }
    }

    abstract fun calculateTotalLengthOf(path: List<Edge<T>>): T

    fun getAdjacencyList(): Map<String, List<Edge<T>>> = adjacencyList

    fun getRandomPath(startNode: Node = nodes.random()): MutableList<Edge<T>> {
        val visited = mutableListOf<Edge<T>>()
        val path = mutableListOf<Edge<T>>()

        fun dfs(node: Node) {
            val suitableEdges = adjacencyList[node.id.toString()] ?: emptyList()
            if (suitableEdges.all { visited.find { edge -> edge.id == it.id } != null }) {
                path.removeLast()
                visited.removeLast()
                return
            }

            for (edge in suitableEdges.shuffled()) {
                if (visited.find { it.id == edge.id } == null) {
                    visited.add(edge)
                    path.add(edge)
                    dfs(edge.destination)
                }
            }
        }

        try {
            dfs(startNode)

            val finalEdge = adjacencyList[path.last().destination.id.toString()]?.find { it.destination.id == startNode.id }
                ?: throw Error("Final edge undefined")
            if (path.size + 1 != adjacencyList.values.sumOf { it.size }) {
                throw IllegalArgumentException("No path found from the given node")
            }
            return path.plus(finalEdge).toMutableList()
        } catch (ex: Exception) {
            throw Error("Error while finding a path: " + ex.message)
        }
    }
}