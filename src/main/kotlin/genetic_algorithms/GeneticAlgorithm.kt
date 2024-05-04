package genetic_algorithms

import genetic_algorithms.operators.*
import graph.Edge
import graph.Graph
import graph.Node
import utils.helpers.LoggingHelper

class GeneticAlgorithm {
    private val logger = LoggingHelper().getLogger(GeneticAlgorithm::class.java.simpleName)

    fun <T> start(
        graph: Graph<T>,
        onFitness: (chromosome: Chromosome<Edge<T>>) -> Double,
        onDistance: (Chromosome<Edge<T>>, Chromosome<Edge<T>>) -> Double,
        configuration: GeneticAlgorithmConfiguration,
    ): Population<Edge<T>> {
        logger.info { "Launching the genetic algorithm" }

        val (
            iterationCount,
            populationSize,
            start,
            parentsConf,
            recombinationType,
            mutation,
            newPopulationConf,
        ) = configuration

        val startNode = if (start == null) {
            graph.nodes.random()
        } else {
            graph.getNodeById(start) ?: graph.nodes.random()
        }

        val paths = MutableList(populationSize) { Chromosome(graph.getRandomPath(startNode).toTypedArray()) }
        var population = Population(paths)

        logger.info { "A starting population has been created (id: ${population.id})" }

        for (i in 0..iterationCount) {
            val allParents = when (parentsConf.selection) {
                SelectionMethods.Types.TOURNAMENT -> {
                    SelectionMethods.tournamentSelection(population, onFitness)
                }
                SelectionMethods.Types.ROULETTE_WHEEL -> {
                    SelectionMethods.rouletteWheelSelection(population, onFitness)
                }
            }
            val selectedParents = when (parentsConf.chooser) {
                ParentSelectionMethods.Types.PANMIXIA -> {
                    ParentSelectionMethods.panmixia(allParents)
                }
                ParentSelectionMethods.Types.INBREEDING -> {
                    ParentSelectionMethods.inbreeding(allParents, onDistance)
                }
                ParentSelectionMethods.Types.OUTBREEDING -> {
                    ParentSelectionMethods.outbreeding(allParents, onDistance)
                }
            }
            val offspring = when (recombinationType) {
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
            }
            if (Math.random() > mutation.rate) {
                offspring.toList().forEach {
                    when (mutation.type) {
                        MutationMethods.Types.REPLACING -> {
                            MutationMethods.replacingMutation(it, graph.edges.toTypedArray())
                        }
                        MutationMethods.Types.SWAPPING -> {
                            MutationMethods.swappingMutation(it)
                        }
                    }
                }
            }

            population.entities.addAll(offspring.toList())

            population = when (newPopulationConf.type) {
                NewPopulationMethods.Types.TRUNCATION -> {
                    NewPopulationMethods.truncationSelection(population, onFitness, newPopulationConf.rate)
                }
                NewPopulationMethods.Types.ELITE -> {
                    NewPopulationMethods.eliteSelection(population, onFitness, newPopulationConf.rate)
                }
                NewPopulationMethods.Types.EXCLUSION -> {
                    NewPopulationMethods.exclusionSelection(population, onFitness)
                }
            }
        }

        return population
    }

    private fun <T> Graph<T>.getNodeById(id: String): Node? {
        return nodes.find { it.id == id }
    }
}
