package genetic_algorithms.operators

import genetic_algorithms.Chromosome

// Набор методов для мутации хромосом
object MutationMethods {
    // Список доступных методов
    enum class Types {
        REPLACING,
        SWAPPING
    }

    // Случайным образом из набора возможных выбирается случайное значение, и встаёт на место случайного гена
    fun <T> replacingMutation(chromosome: Chromosome<T>, possibleValues: Array<T>) {
        val value = possibleValues.randomOrNull() ?: return
        val index = chromosome.genes.indices.random()
        chromosome.genes.apply { this[index] = value }
    }

    // Два случайных гена меняются местами
    fun <T> swappingMutation(chromosome: Chromosome<T>) {
        when {
            chromosome.genes.size <= 3 -> {
                return
            }
            chromosome.genes.size == 4 -> {
                chromosome.genes.reverse()
            }
            else -> {
                with(chromosome) {
                    val index = (2..<(genes.size - 2)).random()
                    genes[index - 1] = genes[index + 1].also { genes[index + 1] = genes[index - 1] }
                }
            }
        }
    }
}