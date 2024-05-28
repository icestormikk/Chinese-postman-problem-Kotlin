package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import graph.Edge
import graph.Graph
import kotlin.random.Random

// Набор методов для мутации хромосом
object MutationMethods {
    // Список доступных методов
    enum class Types {
        REPLACING,
        SWAPPING,
        EDGE_REPLACING
    }

    fun <T, E: Edge<T>> edgeReplacingMutation(chromosome: Chromosome<E>, graph: Graph<T, E>) {
        if (chromosome.genes.size <= 2) return

        val index = if (chromosome.genes.size == 3) 1 else Random.nextInt(1, chromosome.genes.size - 2)
        val commonNodeStart = graph.getCommonNode(chromosome.genes[index - 1], chromosome.genes[index]) ?: return
        val commonNodeEnd = graph.getCommonNode(chromosome.genes[index], chromosome.genes[index + 1]) ?: return
        val newEdge = graph.getEdgesFromTo(commonNodeStart, commonNodeEnd)

        chromosome.genes[index] = newEdge.firstOrNull { it.id != chromosome.genes[index].id } ?: return
    }

    // Случайным образом из набора возможных выбирается случайное значение, и встаёт на место случайного гена
    fun <T> replacingMutation(chromosome: Chromosome<T>, possibleValues: List<T>) {
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