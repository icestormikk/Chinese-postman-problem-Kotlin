package genetic_algorithms

import genetic_algorithms.algorithm.GeneticAlgorithmConfiguration
import genetic_algorithms.algorithm.GeneticAlgorithmHelpers
import genetic_algorithms.entities.base.Chromosome
import graph.Edge
import graph.Graph
import graph.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import utils.constants.ISOLATION_ITERATION_COUNT
import utils.helpers.LoggingHelper

class GeneticAlgorithm {
    // Логгер для отслеживания действий алгоритма в процессе работы программы
    private val logger = LoggingHelper().getLogger(GeneticAlgorithm::class.java.simpleName)

    suspend fun <T: Comparable<T>, E: Edge<T>, G: Graph<T, E>> start(
        // граф, на котором решается задача
        graph: G,
        // функция приспособленности
        onFitness: (chromosome: Chromosome<E>) -> Double,
        // функция для определения близости хромосом
        onDistance: (Chromosome<E>, Chromosome<E>) -> Double,
        // класс с начальными параметрами алгоритма
        configuration: GeneticAlgorithmConfiguration,
        // начальная вершина
        startNode: Node
    ): List<E> {
        logger.info { "Obtaining the parameters of the genetic algorithm" }
        // получаем все необходимые для запуска алгоритма параметры
        val (iterationCount, populationSize, parentsConf, recombinationConf, mutation, newPopulationConf) = configuration

        // находим начальную вершину (если вершина не определена пользователем, то берётся случайная вершина)
        logger.info { "Starting vertex selection: (${startNode.id}, ${startNode.label})" }

        // делим популяцию на подпопуляции
        val populations = GeneticAlgorithmHelpers.Populations.generatePopulations(populationSize, startNode, graph)
        logger.info { "A ${populations.size} populations have been created (${populations[0].entities.size} entities in each)" }

        // задаём начальные значения для лучшего пути и его длины
        logger.info { "Launching the genetic algorithm (${iterationCount} iteration(s))" }
        var bestFitness = Double.NEGATIVE_INFINITY
        var bestChromosome: Chromosome<E>? = null

        // пока не достигнуто максимальное количество итераций (ISOLATION_ITERATION_COUNT - время изоляции)
        for (iteration in 0 until iterationCount step ISOLATION_ITERATION_COUNT) {
            // для каждой из подпопуляций
            val jobs = populations.mapIndexed { index, population ->
                // запускаем свой поток (корутину)
                CoroutineScope(Dispatchers.Default).launch {
                    for (i in iteration until iteration + ISOLATION_ITERATION_COUNT) {
                        // формируем промежуточную популяцию
                        val allParents = GeneticAlgorithmHelpers.Operators.getAllPossibleParents(parentsConf, population, onFitness)
                        // выбираем особей-родителей
                        val selectedParents = GeneticAlgorithmHelpers.Operators.selectParents(parentsConf, allParents, onDistance)
                        // проводим операцию скрещивания
                        val offspring = GeneticAlgorithmHelpers.Operators.getOffspring(recombinationConf, selectedParents, graph, startNode)
                        // применяем оператор мутации
                        GeneticAlgorithmHelpers.Operators.applyMutation(offspring, mutation, graph)
                        // добавляем потомков в общую популяцию
                        population.entities[population.entities.lastIndex - 1] = offspring.first
                        population.entities[population.entities.lastIndex] = offspring.second
                        // создаём новое поколение мутации
                        populations[index] = GeneticAlgorithmHelpers.Operators.getNextPopulation(newPopulationConf, population, onFitness)
                    }
                    population.entities.sortByDescending(onFitness)
                }
            }
            jobs.joinAll()

            // функция для обновления лучшего пути
            @Synchronized fun updateBestPath(currentPath: Chromosome<E>) {
                val fitness = onFitness(currentPath)

                if (bestChromosome == null || fitness > bestFitness) {
                    logger.info { "The best solution has been updated!\tFitness: $fitness" }
                    bestChromosome = currentPath
                    bestFitness = fitness
                }
            }

            // миграция
            val lastPopulationBest = populations.last().entities[0].copy()
            for (populationIndex in 0 until populations.size - 1) {
                val bestInPopulation = populations[populationIndex].entities[0]
                updateBestPath(bestInPopulation)

                populations[populationIndex + 1].entities[0] = bestInPopulation
            }
            populations.first().entities[0] = lastPopulationBest
            updateBestPath(lastPopulationBest)
        }

        if (bestFitness == Double.MAX_VALUE * (-1)) {
            throw IllegalStateException("Couldn't find a edges suitable for the conditions")
        }

        return bestChromosome!!.genes
    }
}
