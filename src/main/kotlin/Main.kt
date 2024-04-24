import common.AlgorithmType
import common.Configuration
import genetic_algorithms.geneticAlgorithm
import graph.Node
import graph.Edge
import graph.Graph
import graph.GraphDao
import particles_swarm.particlesSwarm
import utils.constants.CONFIGURATION_FILE_ARGUMENT
import utils.constants.GRAPH_FILE_ARGUMENT
import utils.helpers.CommandLineHelper
import utils.helpers.FileHelper
import utils.helpers.JSONHelper
import utils.helpers.LoggingHelper
import utils.validators.ConfigurationValidator
import utils.validators.GraphValidator

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
fun GraphDao.toDoubleGraph() = DoubleGraph(nodes, edges.toMutableList())

private val logger = LoggingHelper.getLogger("MAIN_LOGGER")

fun main(args: Array<String>) {
    val arguments = args.toList().chunked(2).associate { it[0] to it[1] }

    val graphDao = CommandLineHelper.fetchArgument(arguments, GRAPH_FILE_ARGUMENT) {
        FileHelper.readFrom(it) { content -> JSONHelper.getInstance().decodeFromString<GraphDao>(content) }
    }
    GraphValidator.validateGraphDao(graphDao)
    val graph = graphDao.toDoubleGraph()

    val configuration = CommandLineHelper.fetchArgument(arguments, CONFIGURATION_FILE_ARGUMENT) {
        FileHelper.readFrom(it) { content -> JSONHelper.getInstance().decodeFromString<Configuration>(content) }
    }
    ConfigurationValidator.validateConfiguration(configuration, graph)

    when (configuration.type) {
        AlgorithmType.GENETIC -> {
            logger.info { "Genetic Algorithm Started" }
            geneticAlgorithm(
                graph,
                onFitness = { chromosome ->
                    1 / graph.calculateTotalLengthOf(chromosome.genes)
                },
                onDistance = { chromosome, chromosome2 ->
                    chromosome.genes.mapIndexed { index, edge -> if (edge.id != chromosome2.genes[index].id) 1 else 0 }
                        .sum().toDouble()
                },
                configuration.genetic!!
            ).also {
                logger.info { "Genetic Algorithm Ended" }
            }
        }
        AlgorithmType.PARTICLES_SWARM -> {
            logger.info { "Particles Swarm Algorithm Started" }
            particlesSwarm(graph, configuration.particleSwarm!!).also {
                logger.info { "Particles Swarm Algorithm Ended" }
            }
        }
        AlgorithmType.ANNEALING -> TODO()
    }
}

