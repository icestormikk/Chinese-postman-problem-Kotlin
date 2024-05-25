package genetic_algorithms

import genetic_algorithms.operators.*
import graph.Edge
import graph.Graph
import graph.Node
import utils.helpers.LoggingHelper

class GeneticAlgorithm {
    // Логгер для отслеживания действий алгоритма в процессе работы программы
    private val logger = LoggingHelper().getLogger(GeneticAlgorithm::class.java.simpleName)

    fun <T> start(
        // граф, на котором решается задача
        graph: Graph<T, Edge<T>>,
        // функция приспособленности
        onFitness: (chromosome: Chromosome<Edge<T>>) -> Double,
        // функция для определения близости хромосом
        onDistance: (Chromosome<Edge<T>>, Chromosome<Edge<T>>) -> Double,
        // класс с начальными параметрами алгоритма
        configuration: GeneticAlgorithmConfiguration,
    ): Array<Edge<T>> {
        logger.info { "Launching the genetic algorithm" }

        // получаем все необходимые для запуска алгоритма параметры
        val (iterationCount, populationSize, startNodeId, parentsConf, recombinationConf, mutation, newPopulationConf) = configuration

        // находим начальную вершину (если вершина не определена пользователем, то берётся случайная вершина)
        val startNode = if (startNodeId == null) {
            graph.nodes.random()
        } else {
            graph.getNodeById(startNodeId) ?: graph.nodes.random()
        }

        // создаём начальную популяцию (набор случайных путей в графе)
        val paths = MutableList(populationSize) { Chromosome(graph.getRandomPath(startNode).toTypedArray()) }
        var population = Population(paths)
        population.entities.sortByDescending(onFitness)

        logger.info { "A starting population has been created (id: ${population.id})" }

        // пока не будет достигнуто максимальное количество итераций
        for (i in 0..iterationCount) {
            // отбираем особей в промежуточную популяцию
            val allParents = when (parentsConf.selection) {
                SelectionMethods.Types.TOURNAMENT -> {
                    SelectionMethods.tournamentSelection(population, onFitness)
                }
                SelectionMethods.Types.ROULETTE_WHEEL -> {
                    SelectionMethods.rouletteWheelSelection(population, onFitness)
                }

                // если пользователь не выбрал метод отбора, то берётся вся исходная популяция
                null -> population
            }

            // выбираем особей-родителей
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

                // вариант по умолчанию - панмиксия (две случайные особи)
                null -> ParentSelectionMethods.panmixia(allParents)
            }

            // проводим операцию скрещивания
            val offspring = if (Math.random() > recombinationConf.rate) {
                selectedParents
            } else {
                when (recombinationConf.type) {
                    RecombinationMethods.Types.HUX_CROSSOVER -> {
                        RecombinationMethods.huxCrossover(selectedParents.first, selectedParents.second)
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

            offspring.toList().forEach {
                if (mutation.type != null && Math.random() < mutation.rate) {
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

            // добавляем потомков в общую популяцию
            population.entities[population.entities.lastIndex - 1] = offspring.first
            population.entities[population.entities.lastIndex] = offspring.second

            // создаём новое поколение мутации
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

                // вариант по умолчанию - все особи проходят в следующее поколение
                null -> population
            }
        }

        // после завершения работы алгоритма сортируем всех особей по значению функции пригодности в порядке убывания
        population.entities.sortByDescending(onFitness)

        // берём самую приспособленную особь и возвращаем её
        val bestChromosome = population.entities[0]

        if (onFitness(bestChromosome) == Double.MAX_VALUE * (-1)) {
            throw IllegalStateException("Couldn't find a path suitable for the conditions")
        }

        return bestChromosome.genes
    }

    private fun <T> Graph<T, Edge<T>>.getNodeById(id: String): Node? {
        return nodes.find { it.id == id }
    }
}
