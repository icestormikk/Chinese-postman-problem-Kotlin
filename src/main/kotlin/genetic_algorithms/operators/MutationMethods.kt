package genetic_algorithms.operators

import genetic_algorithms.Chromosome

object MutationMethods {
    fun <T> replacingMutation(chromosome: Chromosome<T>, possibleValues: Array<T>): Chromosome<T>? {
        val value = possibleValues.randomOrNull() ?: return null
        val index = chromosome.genes.indices.random()
        return Chromosome(chromosome.genes.clone().apply { this[index] = value })
    }

    fun <T> swappingMutation(chromosome: Chromosome<T>): Chromosome<T>? {
        return when {
            chromosome.genes.size <= 1 -> {
                null
            }
            chromosome.genes.size == 2 -> {
                Chromosome(chromosome.genes.reversedArray())
            }
            else -> {
                with(Chromosome(chromosome.genes.clone())) {
                    val index = (1..<(genes.size - 1)).random()
                    genes[index - 1] = genes[index + 1].also { genes[index + 1] = genes[index - 1] }
                    this
                }
            }
        }
    }
}