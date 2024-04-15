import common.AlgorithmType
import genetic_algorithms.Chromosome
import genetic_algorithms.geneticAlgorithm
import genetic_algorithms.outputToFile
import graph.Edge
import graph.Graph
import graph.Node
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import utils.helpers.CommandLineHelper
import utils.helpers.FileHelper
import java.io.FileNotFoundException
import java.nio.file.Files
import kotlin.io.path.Path

class DoubleGraph(nodes: List<Node>, edges: MutableList<Edge<Double>>): Graph<Double>(nodes, edges) {
    override fun calculateTotalLengthOf(path: Array<Edge<Double>>): Double {
        for (index in 0..<path.lastIndex) {
            if (path[index].destination.id != path[index + 1].source.id) {
                return Double.MAX_VALUE
            }
        }
        return path.sumOf { it.weight }
    }
}

@Serializable
data class GraphDao(
    val nodes: List<Node>,
    val edges: List<Edge<Double>>
) {
    fun toDoubleGraph(): DoubleGraph {
        return DoubleGraph(nodes, edges.toMutableList())
    }
}

fun main(args: Array<String>) {
    val arguments = args.toList().chunked(2).associate { it[0] to it[1] }

    val graphInputFilepath = CommandLineHelper.fetchArgument(arguments, "-graph-input-file") {
        if (!Files.exists(Path(it))) {
            throw FileNotFoundException("The file '$it' does not exist.")
        }
        it
    }
    val pathOutputFilepath = CommandLineHelper.fetchArgument(arguments, "-path-output-file") { it }
    val algorithmType = CommandLineHelper.fetchArgument(arguments, "-algorithm-type") {
        AlgorithmType.valueOf(it)
    }

    val graph = FileHelper.readFrom(graphInputFilepath) {
        Json.decodeFromString<GraphDao>(it).toDoubleGraph()
    }
    val onFitness = { chromosome: Chromosome<Edge<Double>> ->
        if (!chromosome.genes.all { graph.edges.any { edge -> edge.id == it.id } }) {
            Double.MIN_VALUE
        }
        (-1) * graph.calculateTotalLengthOf(chromosome.genes)
    }

    when (algorithmType) {
        AlgorithmType.GENETIC -> {
            val result = geneticAlgorithm(graph, 1000, 1000, onFitness = onFitness)
            outputToFile(pathOutputFilepath, result, onFitness) { graph.calculateTotalLengthOf(it) }
        }
        AlgorithmType.PARTICLE_SWARM -> TODO()
        AlgorithmType.ANNEALING -> TODO()
    }
}

