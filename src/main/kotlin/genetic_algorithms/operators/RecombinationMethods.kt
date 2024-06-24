package genetic_algorithms.operators

import genetic_algorithms.entities.base.Chromosome
import graph.Edge
import graph.Graph
import graph.Node
import kotlin.math.min
import kotlin.random.Random

// Набор методов для обмена генами между особями
object RecombinationMethods {
    // Список доступных методов
    enum class Types {
        DISCRETE,
        CHROMOSOME_CROSSOVER,
        HUX_CROSSOVER,
        TWO_POINT_CROSSOVER,
        SINGLE_POINT_CROSSOVER,
        SHUFFLE,
        MODIFIED_SINGLE_POINT
    }

    fun <T, E: Edge<T>, G: Graph<T, E>> modifiedSinglePointCrossover(
        parent1: Chromosome<E>, parent2: Chromosome<E>, graph: G, startNode: Node
    ): Pair<Chromosome<E>, Chromosome<E>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)
        val size = suitableIndexes.last + 1

        // Выбираем две точки разреза
        val point = Random.nextInt(1, size - 1)
        val child1Genes = graph.getRandomPath(startNode, parent1.genes.slice(0..point).toMutableList())
        val child2Genes = graph.getRandomPath(startNode, parent2.genes.slice(0..point).toMutableList())

        return Pair(Chromosome(child1Genes), Chromosome(child2Genes))
    }

    fun <T, E: Edge<T>, G: Graph<T, E>> chromosomeCrossover(
        parent1: Chromosome<E>, parent2: Chromosome<E>, graph: G, startNode: Node
    ): Pair<Chromosome<E>, Chromosome<E>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)
        val size = suitableIndexes.last + 1
        val child1Genes = parent1.genes.toMutableList()
        val child2Genes = parent2.genes.toMutableList()

        // Выбираем две точки разреза
        val (start, end) = if (size == 3) {
            listOf(0, 2)
        } else {
            List(2) { Random.nextInt(1, size - 1) }.sorted()
        }

        // Копируем сегменты между родителями
        for (i in start .. end) {
            child1Genes[i] = parent1.genes[i]
            child2Genes[i] = parent2.genes[i]
        }

        fun getFixedPath(childGenes: MutableList<E>): List<E> {
            val startTo = graph.getCommonNode(childGenes[start], childGenes[start + 1]) ?: childGenes[start].source
            val anotherStartFragment = graph.getPathBetween(startNode, startTo)

            val endFrom = with (graph.getCommonNode(childGenes[end - 1], childGenes[end])) {
                if (childGenes[end].source == this) childGenes[end].destination else childGenes[end].source
            }
            val anotherEndFragment = graph.getPathBetween(endFrom, startNode)

            return (anotherStartFragment ?: child1Genes.slice(0 until start)) +
                        childGenes.slice(start..end) +
                    (anotherEndFragment ?: childGenes.slice((end + 1) until childGenes.lastIndex))
        }

        val child1 = Chromosome(getFixedPath(child1Genes).toMutableList())
        val child2 = Chromosome(getFixedPath(child2Genes).toMutableList())

        return Pair(child1, child2)
    }

    fun <T> huxCrossover(parent1: Chromosome<T>, parent2: Chromosome<T>): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        val length = suitableIndexes.last
        val swapIndices = mutableListOf<Int>()

        // Determine the indices for genes to be swapped
        for (i in 0 until length) {
            if (parent1.genes[i] != parent2.genes[i]) {
                swapIndices.add(i)
            }
        }

        // Select half of the differing indices to swap
        swapIndices.shuffle()
        val halfSize = swapIndices.size / 2
        val indicesToSwap = swapIndices.take(halfSize)

        val child1Genes = parent1.genes.toMutableList()
        val child2Genes = parent2.genes.toMutableList()

        // Swap genes at the selected indices
        for (index in indicesToSwap) {
            child1Genes[index] = parent2.genes[index]
            child2Genes[index] = parent1.genes[index]
        }

        // Create children chromosomes
        val child1 = Chromosome(child1Genes)
        val child2 = Chromosome(child2Genes)

        return Pair(child1, child2)
    }

    /**
     * Дискретная рекомбинация - каждый ген особи - потомка может с равной вероятностью получен от первого или от второго родителя
     * @param parent1 Первый "родитель"
     * @param parent2 Второй "родитель"
     * @return Две особи - потомка с новыми генами
     */
    fun <T> discreteRecombination(parent1: Chromosome<T>, parent2: Chromosome<T>): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        val child1Genes = parent1.genes.toMutableList()
        val child2Genes = parent2.genes.toMutableList()

        for (index in suitableIndexes) {
            child1Genes[index] = if (Math.random() > 0.5) parent1.genes[index] else parent2.genes[index]
        }
        for (index in suitableIndexes) {
            child2Genes[index] = if (Math.random() > 0.5) parent1.genes[index] else parent2.genes[index]
        }

        return Pair(Chromosome(child1Genes), Chromosome(child2Genes))
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

        val child1Genes = parent1.genes.toMutableList()
        val child2Genes = parent2.genes.toMutableList()
        val pointsSet = setOf(points?.first ?: suitableIndexes.random(), points?.second ?: suitableIndexes.random())

        if (pointsSet.size == 1) {
            return singlePointCrossover(parent1, parent2, pointsSet.elementAt(0))
        }

        for (index in pointsSet.elementAt(0)..pointsSet.elementAt(1)) {
            child1Genes[index] = parent2.genes[index]
            child2Genes[index] = parent1.genes[index]
        }

        return Pair(Chromosome(child1Genes), Chromosome(child2Genes))
    }

    /**
     * Одноточечный кроссинговер, хромосомы делятся на две части и обмениваются ими
     * @see twoPointCrossover
     */
    fun <T> singlePointCrossover(
        parent1: Chromosome<T>, parent2: Chromosome<T>, point: Int? = null
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)
        val index = point ?: suitableIndexes.random()

        val offspring1 = Chromosome(
            (parent1.genes.slice(0..<index) + parent2.genes.slice(index..parent2.genes.lastIndex)).toMutableList()
        )
        val offspring2 = Chromosome(
            (parent2.genes.slice(0..<index) + parent1.genes.slice(index..parent1.genes.lastIndex)).toMutableList()
        )

        return Pair(offspring1, offspring2)
    }

    /**
     * Случайное перемешивание генов хромосом. Изменяются и "родители", и "потомки"
     * @param parent1 Первый "родитель"
     * @param parent2 Второй "родитель"
     * @return Две особи - потомка с новыми генами
     */
    fun <T> shuffleCrossover(parent1: Chromosome<T>, parent2: Chromosome<T>): Pair<Chromosome<T>, Chromosome<T>> {
        val suitableIndexes = getSuitableGenesRange(parent1.genes, parent2.genes)

        val shuffleBetween = { p1: Chromosome<T>, p2: Chromosome<T> ->
            val p1Genes = p1.genes.toMutableList()
            val p2Genes = p2.genes.toMutableList()

            for (index in suitableIndexes) {
                if (Math.random() > 0.5) {
                    val temp = p1Genes[index]
                    p1Genes[index] = p2Genes[index]
                    p2Genes[index] = temp
                }
            }

            p1.genes = p1Genes; p2.genes = p2Genes
        }

        shuffleBetween(parent1, parent2)
        val children = singlePointCrossover(parent1, parent2)
        shuffleBetween(children.first, children.second)

        return children
    }

    private fun getSuitableGenesRange(firstGenes: List<*>, secondGenes: List<*>): IntRange {
        return 0..min(firstGenes.lastIndex, secondGenes.lastIndex)
    }
}