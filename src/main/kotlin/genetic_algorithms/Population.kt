package genetic_algorithms

import common.Identifiable

data class Population<T>(
    val entities: MutableList<Chromosome<T>>,
) : Identifiable()