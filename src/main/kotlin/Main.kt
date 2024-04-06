import graph.Edge
import graph.Graph
import graph.Node
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import utils.helpers.FileHelper

class DoubleGraph(
    override val nodes: List<Node>,
    override val edges: List<Edge<Double>>
) : Graph<Double>(nodes, edges) {
    override fun calculateTotalLengthOf(path: List<Edge<Double>>): Double {
        return path.sumOf { it.weight }
    }
}



fun main() {
    val nodes = FileHelper.readFrom("C:\\Users\\jigal\\IdeaProjects\\chinese_postman_problem\\test.txt") {
        Json.decodeFromString<List<Node>>(it)
    }

    println(nodes)
//    val edges = listOf(
//        Edge(nodes[0], nodes[1], Random.nextDouble(0.0, 100.0), EdgeType.DIRECTED),
//        Edge(nodes[1], nodes[2], Random.nextDouble(0.0, 100.0), EdgeType.DIRECTED),
//        Edge(nodes[2], nodes[0], Random.nextDouble(0.0, 100.0), EdgeType.DIRECTED),
//    )
//    val graph = DoubleGraph(nodes, edges)
//
//    val path = graph.getRandomPath(nodes[0])
//    println(path.joinToString(", ") { "${it.source.label} -> ${it.destination.label}" })
//    println(graph.calculateTotalLengthOf(path))
}

