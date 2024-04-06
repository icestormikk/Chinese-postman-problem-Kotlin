import graph.Edge
import graph.Graph
import graph.Node
import utils.constants.EDGES_FILE_PATH_ARGUMENT
import utils.constants.NODES_FILE_PATH_ARGUMENT
import utils.helpers.CommandLineHelper

class DoubleGraph(
    override val nodes: List<Node>,
    override val edges: List<Edge<Double>>
) : Graph<Double>(nodes, edges) {
    override fun calculateTotalLengthOf(path: List<Edge<Double>>): Double {
        return path.sumOf { it.weight }
    }
}



fun main(args: Array<String>) {
    val arguments = args.toList().chunked(2).associate { it[0] to it[1] }

    val nodesFilePath = CommandLineHelper.fetchArgument(arguments, "-${NODES_FILE_PATH_ARGUMENT}") { it }
    val edgesFilePath = CommandLineHelper.fetchArgument(arguments, "-${EDGES_FILE_PATH_ARGUMENT}") { it }

//    val nodes = List(3) { Node("Node-${it}") }
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

