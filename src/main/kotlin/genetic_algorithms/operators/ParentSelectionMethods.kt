package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import genetic_algorithms.Population

object ParentSelectionMethods {
    fun <T> panmixia(population: Population<T>): Pair<Chromosome<T>, Chromosome<T>> {
        val firstParent = population.entities.random()
        val secondParent = population.entities.random()
        return Pair(firstParent, secondParent)
    }

    fun <T> inbreeding(
        population: Population<T>, onDistance: (chr1: Chromosome<T>, chr2: Chromosome<T>) -> Double
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val first = population.entities.random()
        val sortedByDistance = population.entities
            .filter { it.id != first.id }
            .sortedBy { onDistance(first, it) }

        return Pair(first, sortedByDistance[0])
    }

    fun <T> outbreeding(
        population: Population<T>, onDistance: (chr1: Chromosome<T>, chr2: Chromosome<T>) -> Double
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val first = population.entities.random()
        val sortedByDistance = population.entities
            .filter { it.id != first.id }
            .sortedByDescending { onDistance(first, it) }

        return Pair(first, sortedByDistance[0])
    }
}