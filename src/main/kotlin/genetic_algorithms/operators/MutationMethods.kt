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

        val newGenes = chromosome.genes.toMutableList()

        val index = if (newGenes.size == 3) 1 else Random.nextInt(1, newGenes.size - 2)
        val commonNodeStart = graph.getCommonNode(newGenes[index - 1], newGenes[index]) ?: return
        val commonNodeEnd = graph.getCommonNode(newGenes[index], newGenes[index + 1]) ?: return

        if (commonNodeStart == commonNodeEnd) return

        val newEdgeVariants = graph.getEdgesFromTo(commonNodeStart, commonNodeEnd)
        val newEdge = newEdgeVariants.firstOrNull { it.id != newGenes[index].id }

        if (newEdge != null) {
            newGenes[index] = newEdge
        }

        chromosome.genes = newGenes
    }

    // Случайным образом из набора возможных выбирается случайное значение, и встаёт на место случайного гена
    fun <T> replacingMutation(chromosome: Chromosome<T>, possibleValues: List<T>) {
        val newGenes = chromosome.genes.toMutableList()

        val value = possibleValues.randomOrNull() ?: return
        val index = chromosome.genes.indices.random()
        newGenes.apply { this[index] = value }

        chromosome.genes = newGenes
    }

    // Два случайных гена меняются местами
    fun <T> swappingMutation(chromosome: Chromosome<T>) {
        when {
            chromosome.genes.size <= 3 -> {
                return
            }
            chromosome.genes.size == 4 -> {
                chromosome.genes = chromosome.genes.reversed()
            }
            else -> {
                val newGenes = chromosome.genes.toMutableList()

                val index = (2..<(newGenes.size - 2)).random()
                newGenes[index - 1] = newGenes[index + 1].also { newGenes[index + 1] = newGenes[index - 1] }

                chromosome.genes = newGenes
            }
        }
    }

    fun <T, E: Edge<T>> cataclysmicMutation(chromosome: Chromosome<E>, graph: Graph<T, E>) {
        repeat(chromosome.genes.size / 2) {
            edgeReplacingMutation(chromosome, graph)
        }
    }
}