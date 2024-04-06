package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import genetic_algorithms.Population

object SelectionMethods {
    private const val DEFAULT_TOURNAMENT_SIZE = 2

    fun <T> tournamentSelection(
        population: Population<T>,
        onFitness: (chromosome: Chromosome<T>) -> Double,
        tournamentSize: Int = DEFAULT_TOURNAMENT_SIZE
    ): Population<T> {
        require(tournamentSize <= population.entities.size) {
            "The number of individuals participating in the tournament at the same time cannot be greater than the total number of individuals in the population"
        }

        val newEntities = arrayListOf<Chromosome<T>>()

        while (newEntities.size != population.entities.size) {
            val randomEntities = Array(tournamentSize) { population.entities.random() }
            randomEntities.sortByDescending(onFitness)

            newEntities.add(randomEntities[0])
        }

        return Population(newEntities)
    }

    fun <T> rouletteWheelSelection(
        population: Population<T>, onFitness: (chromosome: Chromosome<T>) -> Double
    ): Population<T> {
        val fitnessSum = population.entities.sumOf(onFitness)
        val newEntities = arrayListOf<Chromosome<T>>()

        while (newEntities.size != population.entities.size) {
            val randomRouletteValue = getRandomBetween(0.0, fitnessSum)
            var sum = 0.0

            for (entity in population.entities) {
                sum += onFitness(entity)
                if (sum >= randomRouletteValue) {
                    newEntities.add(entity)
                    break
                }
            }
        }

        return Population(newEntities)
    }

    private fun getRandomBetween(min: Double, max: Double) = min + Math.random() * (max - min)
}