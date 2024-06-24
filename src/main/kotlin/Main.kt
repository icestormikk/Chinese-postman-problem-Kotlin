import ant_colony.AntColonyAlgorithm
import common.AlgorithmType
import common.Configuration
import common.Response
import genetic_algorithms.GeneticAlgorithm
import genetic_algorithms.algorithm.GeneticAlgorithmHelpers
import genetic_algorithms.entities.base.Chromosome
import graph.*
import kotlinx.serialization.encodeToString
import utils.constants.*
import utils.helpers.CommandLineHelper
import utils.helpers.FileHelper
import utils.helpers.JSONHelper
import utils.helpers.LoggingHelper
import utils.validators.ConfigurationValidator
import java.nio.file.Paths
import kotlin.time.measureTimedValue

private fun getDefaultLoggerFilepath() = Paths
    .get(System.getProperty("user.home"), "chinese-postman-problem-program.log")
    .toAbsolutePath()
    .toString()

private val logger = LoggingHelper().getLogger()

suspend fun main(args: Array<String>) {
    // Настраиваем инструмент для логирования
    getDefaultLoggerFilepath().let { filepath ->
        if (System.getProperty(LOGGER_FILE_VM_OPTION) == null) {
            System.setProperty(LOGGER_FILE_VM_OPTION, filepath)
            logger.info { "The user path to the log files is not specified. The standard path is used: ${getDefaultLoggerFilepath()}" }
        } else {
            logger.info { "A custom log path is used: ${System.getProperty(LOGGER_FILE_VM_OPTION)}" }
        }
    }

    // Начало работы алгоритма
    try {
        // Считываем параметры из командной строки
        val arguments = args.toList().chunked(2).associate { it[0] to it[1] }

        // Формируем объект класса GraphDao из информации, полученной из пользовательского файла
        val graphDao = CommandLineHelper().fetchArgument<GraphDao>(arguments, GRAPH_FILE_ARGUMENT, true) {
            // Считываем информацию об объекте из файла пользователя и преобразуем его в объект GraphDao
            FileHelper().readFrom(it) { content -> JSONHelper().getInstance().decodeFromString(content) }
        }!!
        // Преобразуем GraphDao в класс Graph
        val graph = graphDao.toDoubleGraph()
        logger.info {
            "A graph with ${graph.nodes.size} vertices and ${graph.edges.size}(" +
                    "${graph.edges.filter{ it.type == EdgeType.DIRECTED }.size} directed and " +
                    "${graph.edges.filter{ it.type == EdgeType.NOT_ORIENTED }.size} not oriented) edges has been read"
        }

        // Считываем начальные значения параметров алгоритма
        val configuration = CommandLineHelper().fetchArgument(arguments, CONFIGURATION_FILE_ARGUMENT, true) {
            val res = FileHelper().readFrom<Configuration>(it) { content -> JSONHelper().getInstance().decodeFromString(content) }
            ConfigurationValidator().validateConfiguration(res, graph)
            res
        }!!

        // Считываем пользовательский путь к файлу с результатами
        val resultFilepath = CommandLineHelper().fetchArgument(arguments, RESULT_FILE_ARGUMENT, true) { it }!!

        // Вычисляем начальную вершину
        val startNode = if (configuration.startNodeId == null) {
            graph.nodes.random()
        } else {
            GeneticAlgorithmHelpers.Common.getNodeById(configuration.startNodeId, graph) ?: graph.nodes.random()
        }

        val response: Response = when (configuration.type) {
            // Генетический алгоритм
            AlgorithmType.GENETIC -> {
                launchGeneticAlgorithm(configuration, graph, startNode)
            }
            // Метод имитации муравьиной колонии
            AlgorithmType.ANT_COLONY -> {
                launchAntColonyAlgorithm(configuration, graph, startNode)
            }
        }
        logger.info { "The response has been received, the correctness check begins" }

        // проверка полученного ответа
        validateResponse(response, graph, configuration.maxLength, startNode)
        // запись результата
        FileHelper().writeTo(resultFilepath, JSONHelper().getInstance().encodeToString(response))
    } catch (e: Exception) {
        logger.error { e.message }
        throw e
    }
}

private fun <T, E: Edge<T>, G: Graph<T, E>> validateResponse(
    response: Response,
    graph: G,
    maxLength: Double,
    startNode: Node,
) {
    require (response.length < maxLength) { "The length of the optimal path should not exceed the set value: $maxLength" }

    val missedEdges = graph.edges.filter { !response.path.contains(it.id) }

    require (missedEdges.isEmpty()) {
        "Each edge should be included in the graph, but the next ${missedEdges.size} were not found: ${missedEdges.joinToString(", ") { it.id }}"
    }


    val firstEdge = graph.edges.first { it.id == response.path.first() }
    require (firstEdge.source.id == startNode.id || firstEdge.destination.id == startNode.id) {
        "The first edge in the path must contain the initial vertex"
    }

    val endEdge = graph.edges.first { it.id == response.path.last() }
    require (endEdge.source.id == startNode.id || endEdge.destination.id == startNode.id) {
        "The last edge in the path must contain the initial vertex"
    }

    val pathToEdges = response.path.map { edgeId -> graph.edges.find { it.id == edgeId }!! }
    try {
        require(graph.pathToNodeString(pathToEdges, startNode) != "")
    } catch (e: Exception) {
        logger.error { "Invalid path (${e.message})" }
        throw e
    }
}

private suspend fun launchAntColonyAlgorithm(
    configuration: Configuration,
    graph: DoubleGraph,
    startNode: Node
): Response {
    if (configuration.antColony == null) {
        throw Exception("The ant colony method was selected, but the configuration for it was not transmitted")
    }
    val (result, duration) = measureTimedValue {
        AntColonyAlgorithm().start(graph, configuration.antColony)
    }

    val length = graph.calculateTotalLengthOf(result)
    return Response(result.map { it.id }, length, duration.inWholeMilliseconds)
}

private suspend fun launchGeneticAlgorithm(
    configuration: Configuration,
    graph: DoubleGraph,
    startNode: Node
): Response {
    if (configuration.genetic == null) {
        throw Exception("A genetic algorithm was selected, but the configuration for it was not transmitted")
    }

    fun onFitness(chromosome: Chromosome<Edge<Double>>, maxLength: Double): Double {
        if (chromosome.fitness != null)
            return chromosome.fitness!!

        val length = graph.calculateTotalLengthOf(chromosome.genes)
        if (length > maxLength) {
            return WORST_SOLUTION_FITNESS_VALUE
        }

        val allEdgesPassed = graph.edges.all { edge -> chromosome.genes.find { gene -> gene.id == edge.id } != null }
        val fitness = if (!allEdgesPassed) WORST_SOLUTION_FITNESS_VALUE else (-1) * length

        if (chromosome.fitness == null) {
            chromosome.fitness = fitness
        }

        return fitness
    }

    fun onDistance(ch: Chromosome<Edge<Double>>, ch2: Chromosome<Edge<Double>>): Double {
        return ch.genes.mapIndexed { index, edge -> if (edge.id != ch2.genes.getOrNull(index)?.id) 1 else 0 }.sum()
            .toDouble()
    }

    val (result, duration) = measureTimedValue {
        GeneticAlgorithm().start(graph, { chromosome -> onFitness(chromosome, configuration.maxLength) }, ::onDistance, configuration.genetic, startNode)
    }

    val length = graph.calculateTotalLengthOf(result)
    return Response(result.map { it.id }, length, duration.inWholeMilliseconds)
}
