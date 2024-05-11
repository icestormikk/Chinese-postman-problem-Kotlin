import common.AlgorithmType
import common.Configuration
import common.Response
import genetic_algorithms.Chromosome
import genetic_algorithms.GeneticAlgorithm
import graph.Node
import graph.Edge
import graph.Graph
import graph.GraphDao
import particles_swarm.ParticleSwarm
import simulated_annealing.SimulatedAnnealing
import utils.constants.CONFIGURATION_FILE_ARGUMENT
import utils.helpers.CommandLineHelper
import utils.helpers.FileHelper
import utils.helpers.JSONHelper
import utils.helpers.LoggingHelper
import utils.constants.GRAPH_FILE_ARGUMENT
import utils.constants.LOGGER_FILE_VM_OPTION
import utils.constants.RESULT_FILE_ARGUMENT
import kotlinx.serialization.encodeToString
import java.nio.file.Paths
import kotlin.time.measureTimedValue

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

private fun getDefaultLoggerFilepath() = Paths
    .get(System.getProperty("user.home"), "chinese-postman-problem-program.log")
    .toAbsolutePath()
    .toString()

private val logger = LoggingHelper().getLogger()

fun main(args: Array<String>) {
    getDefaultLoggerFilepath().let { filepath ->
        if (System.getProperty(LOGGER_FILE_VM_OPTION) == null) {
            System.setProperty(LOGGER_FILE_VM_OPTION, filepath)
            logger.info { "The user path to the log files is not specified. The standard path is used: ${getDefaultLoggerFilepath()}" }
        } else {
            logger.info { "A custom log path is used: ${System.getProperty(LOGGER_FILE_VM_OPTION)}" }
        }
    }

    try {
        val arguments = args.toList().chunked(2).associate { it[0] to it[1] }

        val configuration = CommandLineHelper().fetchArgument(arguments, CONFIGURATION_FILE_ARGUMENT, true) {
            FileHelper().readFrom<Configuration>(it) { content -> JSONHelper().getInstance().decodeFromString(content) }
        }!!

        val graphDao = CommandLineHelper().fetchArgument(arguments, GRAPH_FILE_ARGUMENT, true) {
            FileHelper().readFrom<GraphDao>(it) { content -> JSONHelper().getInstance().decodeFromString(content) }
        }!!
        val graph = graphDao.toDoubleGraph()

        val resultFilepath = CommandLineHelper().fetchArgument(arguments, RESULT_FILE_ARGUMENT, true) { it }!!

        val response: Response = when (configuration.type) {
            AlgorithmType.GENETIC -> {
                if (configuration.genetic == null) {
                    throw Exception("The configuration for the genetic algorithm was not passed")
                }

                fun onFitness(chromosome: Chromosome<Edge<Double>>): Double {
                    for (edge in graph.edges) {
                        if (!chromosome.genes.contains(edge)) {
                            return Double.MIN_VALUE
                        }
                    }

                    return (-1) * graph.calculateTotalLengthOf(chromosome.genes)
                }
                fun onDistance(ch: Chromosome<Edge<Double>>, ch2: Chromosome<Edge<Double>>): Double {
                    return ch.genes.mapIndexed { index, edge -> if (edge.id != ch2.genes[index].id) 1 else 0 }.sum().toDouble()
                }

                val (result, duration) = measureTimedValue {
                    GeneticAlgorithm().start(
                        graph,
                        ::onFitness,
                        { ch, ch2 -> onDistance(ch, ch2) },
                        configuration.genetic
                    )
                }

                val length = graph.calculateTotalLengthOf(result)
                Response(result.map { it.id }, length, duration.inWholeMilliseconds)
            }
            AlgorithmType.PARTICLES_SWARM -> {
                if (configuration.particleSwarm == null) {
                    throw Exception("The configuration for the particle swarm method was not passed")
                }
                val (result, duration) = measureTimedValue {
                    ParticleSwarm().start(graph, configuration.particleSwarm)
                }

                val length = graph.calculateTotalLengthOf(result.toTypedArray())
                Response(result.map { it.id }, length, duration.inWholeMilliseconds)
            }
            AlgorithmType.ANNEALING -> {
                if (configuration.annealing == null) {
                    throw Exception("The configuration for the annealing method was not passed")
                }
                val selectedNode = graph.nodes.find { it.id == configuration.annealing.selectedNodeId } ?: graph.nodes.random()
                val (result, duration) = measureTimedValue {
                    SimulatedAnnealing().start(
                        graph,
                        configuration.annealing,
                        { state -> graph.calculateTotalLengthOf(state) },
                        { graph.getRandomPath(selectedNode).toTypedArray() },
                        selectedNode
                    )
                }

                val length = graph.calculateTotalLengthOf(result)
                Response(result.map { it.id }, length, duration.inWholeMilliseconds)
            }
        }

        FileHelper().writeTo(resultFilepath, JSONHelper().getInstance().encodeToString(response))
    } catch (e: Exception) {
        logger.error { e.message }
        throw e
    }
}
