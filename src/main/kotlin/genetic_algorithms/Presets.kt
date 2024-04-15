package genetic_algorithms

import genetic_algorithms.operators.ParentSelectionMethods
import genetic_algorithms.operators.RecombinationMethods
import graph.Edge
import graph.Graph
import graph.Node
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import utils.helpers.FileHelper

@Serializable
data class PathDao(
    val score: Double,
    val edges: List<Edge<Double>>
)

fun <T> geneticAlgorithm(
    graph: Graph<T>,
    iterationCount: Int,
    populationSize: Int,
    onFitness: (chromosome: Chromosome<Edge<T>>) -> Double,
    startNode: Node = graph.nodes.first(),
): Population<Edge<T>> {
    val paths = MutableList(populationSize) { Chromosome(graph.getRandomPath(startNode).toTypedArray()) }
    val population = Population(paths)

    for (i in 0..iterationCount) {
        val parents = ParentSelectionMethods.panmixia(population)
        val offspring = RecombinationMethods.singlePointCrossover(parents.first, parents.second)

        with (population.entities) {
            sortByDescending(onFitness)
            this[lastIndex] = offspring.first
            this[lastIndex - 1] = offspring.second
        }
    }

    return population
}

fun outputToFile(
    filepath: String,
    population: Population<Edge<Double>>,
    onFitness: (chromosome: Chromosome<Edge<Double>>) -> Double,
    onDistance: (path: Array<Edge<Double>>) -> Double
) {
    population.entities.sortByDescending(onFitness)
    val best = population.entities.first()
    val pathDao = PathDao(onDistance(best.genes), best.genes.toList())

    FileHelper.writeTo(filepath, Json.encodeToString(pathDao))
}