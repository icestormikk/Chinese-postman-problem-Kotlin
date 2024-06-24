package genetic_algorithms.operators

import genetic_algorithms.entities.base.Chromosome
import genetic_algorithms.entities.base.Population
import kotlin.math.ceil

// Алгоритмы составления новой популяции
object NewPopulationMethods {
    // Список доступных алгоритмов
    enum class Types {
        TRUNCATION,
        ELITE,
        EXCLUSION
    }

    // Отбор усечением (выбор случайных особей из числа пригодных)
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
            .take(truncationEntitiesCount)

        return Population(
            MutableList(population.entities.size) { truncatedEntities.random() }
        )
    }

    // Элитарный отбор (все особи, которые имеют значение пригодности выше установленного, проходят в следующую популяцию)
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
        val remainingEntitiesCount = population.entities.size - suitableEntitiesCount
        val suitableEntities = population.entities
            .sortedByDescending(onFitness)
            .take(suitableEntitiesCount)

        val tournamentEntities = SelectionMethods.tournamentSelection(population, onFitness)

        return Population((suitableEntities + tournamentEntities.entities.slice(0..remainingEntitiesCount)).toMutableList())
    }

    // Отбор вытеснением (то же, что и отбор усечением, но все особи имеют разный набор генов)
    fun <T> exclusionSelection(
        population: Population<T>,
        onFitness: (chromosome: Chromosome<T>) -> Double,
    ): Population<T> {
        val suitableEntities = population.entities.sortedByDescending(onFitness).distinctBy { it.genes }
        return Population(suitableEntities.toMutableList())
    }
}