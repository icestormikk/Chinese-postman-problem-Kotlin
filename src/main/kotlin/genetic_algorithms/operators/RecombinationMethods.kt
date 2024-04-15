package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import kotlin.math.min

object RecombinationMethods {
    private const val BEST_RECOMBINATION_MULTIPLIER = 0.25

    fun <T> discreteRecombination(parent1: Chromosome<T>, parent2: Chromosome<T>): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        val child1 = Chromosome(parent1.genes)
        val child2 = Chromosome(parent2.genes)

        for (index in suitableIndexes) {
            child1.genes[index] = if (Math.random() > 0.5) parent1.genes[index] else parent2.genes[index]
        }
        for (index in suitableIndexes) {
            child2.genes[index] = if (Math.random() > 0.5) parent1.genes[index] else parent2.genes[index]
        }

        return Pair(child1, child2)
    }

    fun intermediateRecombination(
        parent1: Chromosome<Double>, parent2: Chromosome<Double>, parameter: Double = BEST_RECOMBINATION_MULTIPLIER
    ): Pair<Chromosome<Double>, Chromosome<Double>> {
        require(parameter >= 0) { "The parameter value should not be negative" }

        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        val child1 = Chromosome(parent1.genes)
        val child2 = Chromosome(parent2.genes)

        for (index in suitableIndexes) {
            val min = -parameter
            val max = 1 - parameter
            val alpha = min + Math.random() * (max - min)

            child1.genes[index] = parent1.genes[index] + alpha * (parent2.genes[index] - parent1.genes[index])
        }

        for (index in suitableIndexes) {
            val alpha = getRandomBetween(-parameter, 1 - parameter)
            child2.genes[index] = parent1.genes[index] + alpha * (parent2.genes[index] - parent1.genes[index])
        }

        return Pair(child1, child2)
    }

    fun linearRecombination(
        parent1: Chromosome<Double>, parent2: Chromosome<Double>, parameter: Double = BEST_RECOMBINATION_MULTIPLIER
    ): Pair<Chromosome<Double>, Chromosome<Double>> {
        require(parameter >= 0) { "The parameter value should not be negative" }

        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        val child1 = Chromosome(parent1.genes)
        val child2 = Chromosome(parent2.genes)

        var alpha = getRandomBetween(-parameter, 1 - parameter)
        for (index in suitableIndexes) {
            child1.genes[index] = parent1.genes[index] + alpha * (parent2.genes[index] - parent1.genes[index])
        }

        alpha = getRandomBetween(-parameter, 1 - parameter)
        for (index in suitableIndexes) {
            child2.genes[index] = parent1.genes[index] + alpha * (parent2.genes[index] - parent1.genes[index])
        }

        return Pair(child1, child2)
    }

    fun <T> twoPointCrossover(
        parent1: Chromosome<T>, parent2: Chromosome<T>, points: Pair<Int, Int>
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        require(points.toList().all { it > -1 && it <= suitableIndexes.last }) {
            "The crossing points should not be negative or larger than the suitable size of the chromosome"
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

    inline fun <reified T> singlePointCrossover(
        parent1: Chromosome<T>, parent2: Chromosome<T>, point: Int? = null
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)
        val index = point ?: suitableIndexes.random()

        val offspring1 = Chromosome(
            (parent1.genes.slice(0..<index) + parent2.genes.slice(index..parent2.genes.lastIndex)).toTypedArray()
        )
        val offspring2 = Chromosome(
            (parent2.genes.slice(0..<index) + parent1.genes.slice(index..parent1.genes.lastIndex)).toTypedArray()
        )

        return Pair(offspring1, offspring2)
    }

    inline fun <reified T> shuffleCrossover(parent1: Chromosome<T>, parent2: Chromosome<T>): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        val shuffleBetween = { p1: Chromosome<T>, p2: Chromosome<T> ->
            for (index in suitableIndexes) {
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
    fun getSuitableGenesRange(firstGenes: Array<*>, secondGenes: Array<*>): IntRange {
        return 0..min(firstGenes.lastIndex, secondGenes.lastIndex)
    }
}