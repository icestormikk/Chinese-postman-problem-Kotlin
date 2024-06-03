package genetic_algorithms.operators

import genetic_algorithms.entities.base.Chromosome
import graph.Edge
import graph.Graph
import kotlin.random.Random

// Набор методов для мутации хромосом
object MutationMethods {
    // Список доступных методов
    enum class Types {
        REPLACING,
        SWAPPING,
        EDGE_REPLACING,
        CATACLYSMIC
    }

    fun <T, E: Edge<T>> edgeReplacingMutation(chromosome: Chromosome<E>, graph: Graph<T, E>) {
        if (chromosome.genes.size <= 2) return
        chromosome.fitness = null

        val index = if (chromosome.genes.size == 3) 1 else Random.nextInt(1, chromosome.genes.size - 2)
        val commonNodeStart = graph.getCommonNode(chromosome.genes[index - 1], chromosome.genes[index]) ?: return
        val commonNodeEnd = graph.getCommonNode(chromosome.genes[index], chromosome.genes[index + 1]) ?: return

        if (commonNodeStart == commonNodeEnd) return

        val newEdgeVariants = graph.getEdgesFromTo(commonNodeStart, commonNodeEnd)
        val newEdge = newEdgeVariants.firstOrNull { it.id != chromosome.genes[index].id }

        if (newEdge != null) {
            chromosome.genes[index] = newEdge
        }
    }

    // Случайным образом из набора возможных выбирается случайное значение, и встаёт на место случайного гена
    fun <T> replacingMutation(chromosome: Chromosome<T>, possibleValues: List<T>) {
        chromosome.fitness = null
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
                chromosome.fitness = null
                chromosome.genes.reverse()
            }
            else -> {
                chromosome.fitness = null
                with(chromosome) {
                    val index = (2..<(genes.size - 2)).random()
                    genes[index - 1] = genes[index + 1].also { genes[index + 1] = genes[index - 1] }
                }
            }
        }
    }

    fun <T, E: Edge<T>> cataclysmicMutation(chromosome: Chromosome<E>, graph: Graph<T, E>) {
        chromosome.fitness = null
        repeat(chromosome.genes.size / 2) {
            edgeReplacingMutation(chromosome, graph)
        }
    }
}