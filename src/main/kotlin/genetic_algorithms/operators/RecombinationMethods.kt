package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import kotlin.math.min

// Набор методов для обмена генами между особями
object RecombinationMethods {
    // Список доступных методов
    enum class Types {
        DISCRETE,
        TWO_POINT_CROSSOVER,
        SINGLE_POINT_CROSSOVER,
        SHUFFLE
    }

    /**
     * Дискретная рекомбинация - каждый ген особи - потомка может с равной вероятностью получен от первого или от второго родителя
     * @param parent1 Первый "родитель"
     * @param parent2 Второй "родитель"
     * @return Две особи - потомка с новыми генами
     */
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

    /**
     * Двухточечный кроссинговер, обмен участком хросомом, заключенным между двумя точками
     * @param parent1 Первый "родитель"
     * @param parent2 Второй "родитель"
     * @param points Индексы генов, определяющие начало и конец промежутка для обмена
     * @return Две особи - потомка с новыми генами
     */
    fun <T> twoPointCrossover(
        parent1: Chromosome<T>, parent2: Chromosome<T>, points: Pair<Int, Int>? = null
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        if (points != null) {
            require(points.toList().all { it > -1 && it <= suitableIndexes.last }) {
                "The crossing points should not be negative or larger than the suitable size of the chromosome"
            }
        }

        val child1 = Chromosome(parent1.genes.clone())
        val child2 = Chromosome(parent2.genes.clone())
        val pointsSet = setOf(points?.first ?: suitableIndexes.random(), points?.second ?: suitableIndexes.random())

        for (index in pointsSet.elementAt(0)..pointsSet.elementAt(1)) {
            child1.genes[index] = parent2.genes[index]
            child2.genes[index] = parent1.genes[index]
        }

        return Pair(child1, child2)
    }

    /**
     * Одноточечный кроссинговер, хромосомы делятся на две части и обмениваются ими
     * @see twoPointCrossover
     */
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

    /**
     * Случайное перемешивание генов хромосом. Изменяются и "родители", и "потомки"
     * @param parent1 Первый "родитель"
     * @param parent2 Второй "родитель"
     * @return Две особи - потомка с новыми генами
     */
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

    fun getSuitableGenesRange(firstGenes: Array<*>, secondGenes: Array<*>): IntRange {
        return 0..min(firstGenes.lastIndex, secondGenes.lastIndex)
    }
}