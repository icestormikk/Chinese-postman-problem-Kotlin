package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import genetic_algorithms.Population
import kotlin.math.ceil

object NewPopulationMethods {
    enum class Types {
        TRUNCATION,
        ELITE,
        EXCLUSION
    }

    fun <T> truncationSelection(
        population: Population<T>,
        onFitness: (chromosome: Chromosome<T>) -> Double,
        truncationPercent: Double
    ): Population<T> {
        require(truncationPercent >= 0) { "The percentage of individuals who will be selected cannot be negative" }

        val truncationEntitiesCount =
            if (truncationPercent > 1)
                population.entities.size
            else ceil(population.entities.size * truncationPercent).toInt()
        val truncatedEntities = population.entities
            .sortedByDescending(onFitness)
            .slice(0..truncationEntitiesCount)

        return Population(
            MutableList(population.entities.size) { truncatedEntities.random() }
        )
    }

    fun <T> eliteSelection(
        population: Population<T>,
        onFitness: (chromosome: Chromosome<T>) -> Double,
        elitePercent: Double
    ): Population<T> {
        require(elitePercent >= 0) { "The percentage of individuals who will be selected cannot be negative" }

        val suitableEntitiesCount =
            if (elitePercent > 1)
                population.entities.size
            else ceil(population.entities.size * elitePercent).toInt()
        val suitableEntities = population.entities
            .sortedByDescending(onFitness)
            .slice(0..<suitableEntitiesCount)

        return Population(suitableEntities.toMutableList())
    }

    fun <T> exclusionSelection(
        population: Population<T>,
        onFitness: (chromosome: Chromosome<T>) -> Double,
    ): Population<T> {
        val suitableEntities = population.entities.sortedByDescending(onFitness).distinctBy { it.genes }
        return Population(suitableEntities.toMutableList())
    }
}