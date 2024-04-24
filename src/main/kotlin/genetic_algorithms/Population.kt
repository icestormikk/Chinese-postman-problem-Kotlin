package genetic_algorithms

import common.Identifiable

data class Population<T>(
    val entities: MutableList<Chromosome<T>>,
) : Identifiable() {
    init {
        require(entities.all { it.genes.isNotEmpty() }) {
            "A population cannot contain chromosomes without genes"
        }
    }
}