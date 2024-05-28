package genetic_algorithms

import genetic_algorithms.operators.*
import graph.Edge
import graph.Graph
import graph.Node
import kotlinx.coroutines.*
import utils.constants.ISOLATION_ITERATION_COUNT
import utils.helpers.LoggingHelper

class GeneticAlgorithm {
    // Логгер для отслеживания действий алгоритма в процессе работы программы
    private val logger = LoggingHelper().getLogger(GeneticAlgorithm::class.java.simpleName)

    suspend fun <T, E: Edge<T>, G: Graph<T, E>> start(
        // граф, на котором решается задача
        graph: G,
        // функция приспособленности
        onFitness: (chromosome: Chromosome<E>) -> Double,
        // функция для определения близости хромосом
        onDistance: (Chromosome<E>, Chromosome<E>) -> Double,
        // класс с начальными параметрами алгоритма
        configuration: GeneticAlgorithmConfiguration,
    ): MutableList<E> {
        logger.info { "Obtaining the parameters of the genetic algorithm" }
        // получаем все необходимые для запуска алгоритма параметры
        val (iterationCount, populationSize, startNodeId, parentsConf, recombinationConf, mutation, newPopulationConf) = configuration

        // находим начальную вершину (если вершина не определена пользователем, то берётся случайная вершина)
        val startNode = if (startNodeId == null) {
            graph.nodes.random()
        } else {
            graph.getNodeById(startNodeId) ?: graph.nodes.random()
        }
        logger.info { "Starting vertex selection: (${startNode.id}, ${startNode.label})" }

        val populations = generatePopulations(populationSize, startNode, graph)
        logger.info { "A ${populations.size} populations have been created (${populations[0].entities.size} entities in each)" }

        logger.info { "Launching the genetic algorithm (${iterationCount} iteration(s))" }
        var bestFitness = Double.NEGATIVE_INFINITY
        var bestChromosome: Chromosome<E>? = null

        for (iteration in 0 until iterationCount step ISOLATION_ITERATION_COUNT) {
            val jobs = populations.mapIndexed { index, population ->
                CoroutineScope(Dispatchers.Default).launch {
                    for (i in iteration until iteration + ISOLATION_ITERATION_COUNT) {
                        // формируем промежуточную популяцию
                        val allParents = getAllPossibleParents(parentsConf, population, onFitness)
                        // выбираем особей-родителей
                        val selectedParents = selectParents(parentsConf, allParents, onDistance)
                        // проводим операцию скрещивания
                        val offspring = getOffspring(recombinationConf, selectedParents, graph, startNode)
                        // применяем оператор мутации
                        applyMutation(offspring, mutation, graph)
                        // добавляем потомков в общую популяцию
                        population.entities[population.entities.lastIndex - 1] = offspring.first
                        population.entities[population.entities.lastIndex] = offspring.second
                        // создаём новое поколение мутации
                        populations[index] = getNextPopulation(newPopulationConf, population, onFitness)
                    }
                    population.entities.sortByDescending(onFitness)
                }
            }
            jobs.joinAll()

            // миграция
            val lastPopulationBest = populations.last().entities[0].copy()
            for (populationIndex in 0 until populations.size - 1) {
                val bestInPopulation = populations[populationIndex].entities[0]

                val bestPopulationFitness = onFitness(bestInPopulation)
                if (bestChromosome == null || bestFitness < bestPopulationFitness) {
                    bestChromosome = bestInPopulation
                    bestFitness = bestPopulationFitness
                }

                populations[populationIndex + 1].entities[0] = bestInPopulation
            }
            populations.first().entities[0] = lastPopulationBest
        }

        if (bestFitness == Double.MAX_VALUE * (-1)) {
            throw IllegalStateException("Couldn't find a path suitable for the conditions")
        }

        return bestChromosome!!.genes
    }

    private fun <E : Edge<T>, T> getNextPopulation(
        newPopulationConf: PopulationSelectionConfiguration,
        population: Population<E>,
        onFitness: (chromosome: Chromosome<E>) -> Double
    ) = when (newPopulationConf.type) {
        NewPopulationMethods.Types.TRUNCATION -> {
            NewPopulationMethods.truncationSelection(population, onFitness, newPopulationConf.rate)
        }

        NewPopulationMethods.Types.ELITE -> {
            NewPopulationMethods.eliteSelection(population, onFitness, newPopulationConf.rate)
        }

        NewPopulationMethods.Types.EXCLUSION -> {
            NewPopulationMethods.exclusionSelection(population, onFitness)
        }

        // вариант по умолчанию - все особи проходят в следующее поколение
        null -> population
    }

    private fun <E : Edge<T>, T> getAllPossibleParents(
        parentsConf: ParentsConfiguration,
        population: Population<E>,
        onFitness: (chromosome: Chromosome<E>) -> Double
    ) = when (parentsConf.selection) {
        SelectionMethods.Types.TOURNAMENT -> {
            SelectionMethods.tournamentSelection(population, onFitness)
        }

        SelectionMethods.Types.ROULETTE_WHEEL -> {
            SelectionMethods.rouletteWheelSelection(population, onFitness)
        }

        // если пользователь не выбрал метод отбора, то берётся вся исходная популяция
        null -> population
    }

    private fun <E : Edge<T>, T> selectParents(
        parentsConf: ParentsConfiguration,
        allParents: Population<E>,
        onDistance: (Chromosome<E>, Chromosome<E>) -> Double
    ) = when (parentsConf.chooser) {
        ParentSelectionMethods.Types.PANMIXIA -> {
            ParentSelectionMethods.panmixia(allParents)
        }

        ParentSelectionMethods.Types.INBREEDING -> {
            ParentSelectionMethods.inbreeding(allParents, onDistance)
        }

        ParentSelectionMethods.Types.OUTBREEDING -> {
            ParentSelectionMethods.outbreeding(allParents, onDistance)
        }

        // вариант по умолчанию - панмиксия (две случайные особи)
        null -> ParentSelectionMethods.panmixia(allParents)
    }

    private fun <T, E : Edge<T>, G : Graph<T, E>> getOffspring(
        recombinationConf: RecombinationConfiguration,
        selectedParents: Pair<Chromosome<E>, Chromosome<E>>,
        graph: G,
        startNode: Node
    ) = if (Math.random() > recombinationConf.rate) {
        selectedParents
    } else {
        when (recombinationConf.type) {
            RecombinationMethods.Types.CHROMOSOME_CROSSOVER -> {
                RecombinationMethods.chromosomeCrossover(selectedParents.first, selectedParents.second, graph, startNode)
            }

            RecombinationMethods.Types.DISCRETE -> {
                RecombinationMethods.discreteRecombination(selectedParents.first, selectedParents.second)
            }

            RecombinationMethods.Types.TWO_POINT_CROSSOVER -> {
                RecombinationMethods.twoPointCrossover(selectedParents.first, selectedParents.second)
            }

            RecombinationMethods.Types.SINGLE_POINT_CROSSOVER -> {
                RecombinationMethods.singlePointCrossover(selectedParents.first, selectedParents.second)
            }

            RecombinationMethods.Types.SHUFFLE -> {
                RecombinationMethods.shuffleCrossover(selectedParents.first, selectedParents.second)
            }

            // вариант по умолчанию - потомки не создаются
            null -> selectedParents
        }
    }

    private fun <T, E : Edge<T>, G : Graph<T, E>> applyMutation(
        offspring: Pair<Chromosome<E>, Chromosome<E>>,
        mutation: MutationConfiguration,
        graph: G
    ) {
        offspring.toList().forEach {
            if (mutation.type != null && Math.random() < mutation.rate) {
                when (mutation.type) {
                    MutationMethods.Types.REPLACING -> {
                        MutationMethods.replacingMutation(it, graph.edges)
                    }

                    MutationMethods.Types.SWAPPING -> {
                        MutationMethods.swappingMutation(it)
                    }
                }
            }
        }
    }

    private fun <T, E: Edge<T>, G: Graph<T, E>> G.getNodeById(id: String): Node? {
        return nodes.find { it.id == id }
    }

    private suspend fun <T, E: Edge<T>, G: Graph<T, E>> generatePopulations(
        populationSize: Int, startNode: Node, graph: G
    ): MutableList<Population<E>> {
        val entitiesByCoroutine = when (populationSize) {
            in 0..1000 -> 50
            else -> 1000
        }

        val populations = mutableListOf<Population<E>>()
        val jobs = List(populationSize / entitiesByCoroutine) {
            CoroutineScope(Dispatchers.Default).launch {
                val entities = mutableListOf<Chromosome<E>>()
                for (i in 0 until entitiesByCoroutine) {
                    entities.add(Chromosome(graph.getRandomPath(startNode)))
                }

                populations.add(Population(entities))
            }
        }
        jobs.joinAll()

        return populations
    }
}
