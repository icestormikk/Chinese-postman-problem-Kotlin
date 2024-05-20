import ant_colony.AntColonyAlgorithm
import common.AlgorithmType
import common.Configuration
import common.Response
import genetic_algorithms.Chromosome
import genetic_algorithms.GeneticAlgorithm
import graph.Node
import graph.Edge
import graph.Graph
import graph.GraphDao
import utils.constants.CONFIGURATION_FILE_ARGUMENT
import utils.helpers.CommandLineHelper
import utils.helpers.FileHelper
import utils.helpers.JSONHelper
import utils.helpers.LoggingHelper
import utils.constants.GRAPH_FILE_ARGUMENT
import utils.constants.LOGGER_FILE_VM_OPTION
import utils.constants.RESULT_FILE_ARGUMENT
import kotlinx.serialization.encodeToString
import utils.validators.ConfigurationValidator
import java.nio.file.Paths
import kotlin.time.measureTimedValue

class DoubleGraph(nodes: List<Node>, edges: MutableList<Edge<Double>>): Graph<Double, Edge<Double>>(nodes, edges) {
    override fun calculateTotalLengthOf(path: Array<Edge<Double>>): Double {
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

        val graphDao = CommandLineHelper().fetchArgument<GraphDao>(arguments, GRAPH_FILE_ARGUMENT, true) {
            FileHelper().readFrom(it) { content -> JSONHelper().getInstance().decodeFromString(content) }
        }!!
        val graph = graphDao.toDoubleGraph()

        val configuration = CommandLineHelper().fetchArgument(arguments, CONFIGURATION_FILE_ARGUMENT, true) {
            val res = FileHelper().readFrom<Configuration>(it) { content -> JSONHelper().getInstance().decodeFromString(content) }
            ConfigurationValidator().validateConfiguration(res, graph)
            res
        }!!

        val resultFilepath = CommandLineHelper().fetchArgument(arguments, RESULT_FILE_ARGUMENT, true) { it }!!

        val response: Response = when (configuration.type) {
            AlgorithmType.GENETIC -> {
                if (configuration.genetic == null) {
                    throw Exception("The configuration for the genetic algorithm was not passed")
                }

                fun onFitness(chromosome: Chromosome<Edge<Double>>): Double {
                    if (!graph.edges.all { chromosome.genes.contains(it) }) {
                        return Double.MIN_VALUE
                    }

                    return (-1) * graph.calculateTotalLengthOf(chromosome.genes)
                }
                fun onDistance(ch: Chromosome<Edge<Double>>, ch2: Chromosome<Edge<Double>>): Double {
                    return ch.genes.mapIndexed { index, edge -> if (edge.id != ch2.genes[index].id) 1 else 0 }.sum().toDouble()
                }

                val (result, duration) = measureTimedValue {
                    GeneticAlgorithm().start(graph, ::onFitness, ::onDistance, configuration.genetic)
                }

                val length = graph.calculateTotalLengthOf(result)
                Response(result.map { it.id }, length, duration.inWholeMilliseconds)
            }
            AlgorithmType.ANT_COLONY -> {
                if (configuration.antColony == null) {
                    throw Exception("The configuration for the annealing method was not passed")
                }
                val (result, duration) = measureTimedValue {
                    AntColonyAlgorithm().start(graph, configuration.antColony)
                }

                val length = graph.calculateTotalLengthOf(result.toTypedArray())
                Response(result.map { it.id }, length, duration.inWholeMilliseconds)
            }
        }

        FileHelper().writeTo(resultFilepath, JSONHelper().getInstance().encodeToString(response))
    } catch (e: Exception) {
        logger.error { e.message }
        throw e
    }
}
