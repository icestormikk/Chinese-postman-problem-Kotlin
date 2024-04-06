package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import utils.constants.GENES_EQUAL_SIZE_MESSAGE

object RecombinationMethods {
    private const val BEST_RECOMBINATION_MULTIPLIER = 0.25

    fun <T> discreteRecombination(parent1: Chromosome<T>, parent2: Chromosome<T>): Pair<Chromosome<T>, Chromosome<T>> {
        require(parent1.genes.size == parent2.genes.size) { GENES_EQUAL_SIZE_MESSAGE }

        val child1 = Chromosome(parent1.genes)
        val child2 = Chromosome(parent2.genes)

        for (index in parent1.genes.indices) {
            child1.genes[index] = if (Math.random() > 0.5) parent1.genes[index] else parent2.genes[index]
        }
        for (index in parent2.genes.indices) {
            child2.genes[index] = if (Math.random() > 0.5) parent1.genes[index] else parent2.genes[index]
        }

        return Pair(child1, child2)
    }

    fun intermediateRecombination(
        parent1: Chromosome<Double>, parent2: Chromosome<Double>, parameter: Double = BEST_RECOMBINATION_MULTIPLIER
    ): Pair<Chromosome<Double>, Chromosome<Double>> {
        require(parent1.genes.size == parent2.genes.size) { GENES_EQUAL_SIZE_MESSAGE }
        require(parameter >= 0) { "The parameter value should not be negative" }

        val child1 = Chromosome(parent1.genes)
        val child2 = Chromosome(parent2.genes)

        for (index in parent1.genes.indices) {
            val min = -parameter
            val max = 1 - parameter
            val alpha = min + Math.random() * (max - min)

            child1.genes[index] = parent1.genes[index] + alpha * (parent2.genes[index] - parent1.genes[index])
        }

        for (index in parent2.genes.indices) {
            val alpha = getRandomBetween(-parameter, 1 - parameter)
            child2.genes[index] = parent1.genes[index] + alpha * (parent2.genes[index] - parent1.genes[index])
        }

        return Pair(child1, child2)
    }

    fun linearRecombination(
        parent1: Chromosome<Double>, parent2: Chromosome<Double>, parameter: Double = BEST_RECOMBINATION_MULTIPLIER
    ): Pair<Chromosome<Double>, Chromosome<Double>> {
        require(parent1.genes.size == parent2.genes.size) { GENES_EQUAL_SIZE_MESSAGE }
        require(parameter >= 0) { "The parameter value should not be negative" }

        val child1 = Chromosome(parent1.genes)
        val child2 = Chromosome(parent2.genes)

        var alpha = getRandomBetween(-parameter, 1 - parameter)
        for (index in parent1.genes.indices) {
            child1.genes[index] = parent1.genes[index] + alpha * (parent2.genes[index] - parent1.genes[index])
        }

        alpha = getRandomBetween(-parameter, 1 - parameter)
        for (index in parent2.genes.indices) {
            child2.genes[index] = parent1.genes[index] + alpha * (parent2.genes[index] - parent1.genes[index])
        }

        return Pair(child1, child2)
    }

    fun <T> twoPointCrossover(
        parent1: Chromosome<T>, parent2: Chromosome<T>, points: Pair<Int, Int>
    ): Pair<Chromosome<T>, Chromosome<T>> {
        require(parent1.genes.size == parent2.genes.size) { GENES_EQUAL_SIZE_MESSAGE }
        require(points.toList().all { it > -1 && it < parent1.genes.size }) {
            "The crossing points should not be negative or larger than the size of the chromosome"
        }

        val child1 = Chromosome(parent1.genes.clone())
        val child2 = Chromosome(parent2.genes.clone())
        val pointsSet = setOf(points.first, points.second)

        for (index in pointsSet.elementAt(0)..pointsSet.elementAt(1)) {
            child1.genes[index] = parent2.genes[index]
            child2.genes[index] = parent1.genes[index]
        }

        return Pair(child1, child2)
    }

    fun <T> singlePointCrossover(
        parent1: Chromosome<T>, parent2: Chromosome<T>, point: Int? = null
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val index = point ?: (0..<(parent1.genes.size - 1)).random()
        return twoPointCrossover(parent1, parent2, Pair(index, parent1.genes.size - 1))
    }

    fun <T> shuffleCrossover(parent1: Chromosome<T>, parent2: Chromosome<T>): Pair<Chromosome<T>, Chromosome<T>> {
        require(parent1.genes.size == parent2.genes.size) { GENES_EQUAL_SIZE_MESSAGE }

        val shuffleBetween = { p1: Chromosome<T>, p2: Chromosome<T> ->
            assert(p1.genes.size == p2.genes.size)
            for (index in p1.genes.indices) {
                if (Math.random() > 0.5) {
                    val temp = p1.genes[index]
                    p1.genes[index] = p2.genes[index]
                    p2.genes[index] = temp
                }
            }
        }

        shuffleBetween(parent1, parent2)
        val children = singlePointCrossover(parent1, parent2)
        shuffleBetween(children.first, children.second)

        return children
    }

    private fun getRandomBetween(min: Double, max: Double) = min + Math.random() * (max - min)
}