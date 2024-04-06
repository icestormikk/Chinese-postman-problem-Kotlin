package genetic_algorithms

import common.Identifiable

data class Population<T>(
    val entities: MutableList<Chromosome<T>>,
) : Identifiable() {
    init {
        require(entities.all { it.genes.size == entities[0].genes.size }) { "All chromosomes must be the same length" }
    }
}