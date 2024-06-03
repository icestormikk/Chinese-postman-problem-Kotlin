package genetic_algorithms.algorithm

import genetic_algorithms.entities.base.Chromosome
import genetic_algorithms.entities.base.Population
import genetic_algorithms.operators.*
import graph.Edge
import graph.Graph
import graph.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

object GeneticAlgorithmHelpers {
    object Common {
        fun <T, E: Edge<T>, G: Graph<T, E>> getNodeById(id: String, graph: G): Node? {
            return graph.nodes.find { it.id == id }
        }

        fun getEntitiesCountForCoroutine(populationSize: Int): Int {
            return when (populationSize) {
                in 0 until 100 -> populationSize
                in 100 until 1000 -> populationSize / 10
                else -> populationSize / 100
            }
        }
    }

    object Populations {
        suspend fun <T, E: Edge<T>, G: Graph<T, E>> generatePopulations(
            populationSize: Int, startNode: Node, graph: G
        ): MutableList<Population<E>> {
            val entitiesByCoroutine = Common.getEntitiesCountForCoroutine(populationSize)

            val populations = mutableListOf<Population<E>>()
            val randomSuitablePath = graph.getRandomPath(startNode)

            val jobs = List(populationSize / entitiesByCoroutine) {
                CoroutineScope(Dispatchers.Default).launch {
                    val entities = mutableListOf<Chromosome<E>>()
                    for (i in 0 until entitiesByCoroutine) {
                        val pathCopy = randomSuitablePath.toMutableList()
                        val chromosome = Chromosome(pathCopy)

                        MutationMethods.cataclysmicMutation(chromosome, graph)
                        entities.add(chromosome)
                    }

                    populations.add(Population(entities))
                }
            }
            jobs.joinAll()

            return populations
        }
    }

    object Operators {
        internal fun <E : Edge<T>, T> getNextPopulation(
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

        internal fun <T, E : Edge<T>> getAllPossibleParents(
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

        internal fun <E : Edge<T>, T> selectParents(
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

        internal fun <T, E : Edge<T>, G : Graph<T, E>> getOffspring(
            recombinationConf: RecombinationConfiguration,
            selectedParents: Pair<Chromosome<E>, Chromosome<E>>,
            graph: G,
            startNode: Node
        ) = if (Math.random() > recombinationConf.rate) {
            selectedParents
        } else {
            when (recombinationConf.type) {
                RecombinationMethods.Types.HUX_CROSSOVER -> {
                    RecombinationMethods.huxCrossover(selectedParents.first, selectedParents.second)
                }
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

        internal fun <T, E : Edge<T>, G : Graph<T, E>> applyMutation(
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
                        MutationMethods.Types.EDGE_REPLACING -> {
                            MutationMethods.edgeReplacingMutation(it, graph)
                        }
                        MutationMethods.Types.CATACLYSMIC -> {
                            MutationMethods.cataclysmicMutation(it, graph)
                        }
                    }
                }
            }
        }
    }
}