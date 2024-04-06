package genetic_algorithms

import common.Identifiable

data class Chromosome<T>(
    val genes: Array<T>,
) : Identifiable() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chromosome<*>

        if (id != other.id) return false
        return genes.contentEquals(other.genes)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + genes.contentHashCode()
        return result
    }
}
