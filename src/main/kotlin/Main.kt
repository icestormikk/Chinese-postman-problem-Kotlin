import ant_colony.AntColonyAlgorithm
import common.AlgorithmType
import common.Configuration
import common.Response
import genetic_algorithms.Chromosome
import genetic_algorithms.GeneticAlgorithm
import graph.Edge
import graph.GraphDao
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

fun main(args: Array<String>) {
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
        logger.info { "A graph with ${graph.nodes.size} vertices and ${graph.edges.size} edges has been read" }

        // Считываем начальные значения параметров алгоритма
        val configuration = CommandLineHelper().fetchArgument(arguments, CONFIGURATION_FILE_ARGUMENT, true) {
            val res = FileHelper().readFrom<Configuration>(it) { content -> JSONHelper().getInstance().decodeFromString(content) }
            ConfigurationValidator().validateConfiguration(res, graph)
            res
        }!!

        // Считываем пользовательский путь к файлу с результатами
        val resultFilepath = CommandLineHelper().fetchArgument(arguments, RESULT_FILE_ARGUMENT, true) { it }!!

        // Переменная для записи результата работы алгоритма
        val response: Response = when (configuration.type) {
            // Генетический алгоритм
            AlgorithmType.GENETIC -> {
                launchGeneticAlgorithm(configuration, graph)
            }
            // Метод имитации муравьиной колонии
            AlgorithmType.ANT_COLONY -> {
                launchAntColonyAlgorithm(configuration, graph)
            }
        }

        // проверка полученного ответа
        validateResponse(response, configuration.maxLength)

        // запись результата
        FileHelper().writeTo(resultFilepath, JSONHelper().getInstance().encodeToString(response))
    } catch (e: Exception) {
        logger.error { e.message }
        throw e
    }
}

private fun validateResponse(response: Response, maxLength: Double) {
    require(response.length < maxLength) { "The length of the optimal path should not exceed the set value: $maxLength" }
}

private fun launchAntColonyAlgorithm(configuration: Configuration, graph: DoubleGraph): Response {
    if (configuration.antColony == null) {
        throw Exception("The ant colony method was selected, but the configuration for it was not transmitted")
    }
    val (result, duration) = measureTimedValue {
        AntColonyAlgorithm().start(graph, configuration.antColony)
    }

    val length = graph.calculateTotalLengthOf(result.toTypedArray())
    return Response(result.map { it.id }, length, duration.inWholeMilliseconds)
}

private fun launchGeneticAlgorithm(configuration: Configuration, graph: DoubleGraph): Response {
    if (configuration.genetic == null) {
        throw Exception("A genetic algorithm was selected, but the configuration for it was not transmitted")
    }

    fun onFitness(chromosome: Chromosome<Edge<Double>>): Double {
        if (!graph.edges.all { edge -> chromosome.genes.find { gene -> gene.id == edge.id } != null }) {
            return WORST_SOLUTION_FITNESS_VALUE
        }

        return (-1) * graph.calculateTotalLengthOf(chromosome.genes)
    }

    fun onDistance(ch: Chromosome<Edge<Double>>, ch2: Chromosome<Edge<Double>>): Double {
        return ch.genes.mapIndexed { index, edge -> if (edge.id != ch2.genes.getOrNull(index)?.id) 1 else 0 }.sum()
            .toDouble()
    }

    val (result, duration) = measureTimedValue {
        GeneticAlgorithm().start(graph, ::onFitness, ::onDistance, configuration.genetic)
    }

    val length = graph.calculateTotalLengthOf(result)
    return Response(result.map { it.id }, length, duration.inWholeMilliseconds)
}
