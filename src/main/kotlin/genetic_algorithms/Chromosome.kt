package genetic_algorithms

import common.Identifiable

data class Chromosome<T>(
    val genes: MutableList<T>,
) : Identifiable()
