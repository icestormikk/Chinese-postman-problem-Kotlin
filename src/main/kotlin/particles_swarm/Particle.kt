package particles_swarm

import common.Identifiable
import graph.Edge
import graph.Graph
import graph.Node
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.times

/**
 * Класс, описывающий одну частицу
 * @property swarm Рой, которому принадлежит частица
 * @property graph Граф, на котором происходит решение задачи
 * @property startNode Вершина для которой решается задача. Все искомые пути должны начинаться и заканчиваться в ней
 */
data class Particle<T>(
    val swarm: Swarm<T>,
    val graph: Graph<T, Edge<T>>,
    val startNode: Node = graph.nodes.random()
) : Identifiable() {
    // текущая позиция частицы
    var position = initializePosition()
    // лучшая позиция частицы
    var localBestPosition = position
    // значение ф-ции пригодности в лучшей позиции частицы
    var localBestFitnessValue = swarm.calculateFitness(position)

    private fun initializePosition(): List<Edge<T>> {
        return graph.getRandomPath(startNode)
    }

    // обновление позиции частицы
    fun nextIteration(swarm: Swarm<T>) {
        if (swarm.globalBestPosition == null) return

        val randomIndex = (1..<graph.edges.lastIndex).random()

        val temp = graph.edges[randomIndex - 1]
        graph.edges[randomIndex - 1] = graph.edges[randomIndex + 1]
        graph.edges[randomIndex + 1] = temp

        // рассчёт нового локального значения функции приспособленности и обновление лучших показателей при необходимости
        val fitness = swarm.calculateFitness(position)
        if (fitness > localBestFitnessValue) {
            localBestFitnessValue = fitness
            localBestPosition = position
        }
    }
}

private operator fun Array<Double>.plus(target: Array<Int>): Array<Int> {
    val indexes = 0..<min(this.size, target.size)
    return indexes.map { index -> (this[index] + target[index]).toInt() }.toTypedArray()
}

private operator fun Array<Int>.plus(target: Array<Double>): Array<Int> {
    val indexes = 0..<min(this.size, target.size)
    return indexes.map { index -> (this[index] + target[index]).toInt() }.toTypedArray()
}

private operator fun Array<Double>.plus(target: Array<Double>): Array<Double> {
    val indexes = 0..<min(this.size, target.size)
    return indexes.map { index -> this[index] + target[index] }.toTypedArray()
}

private operator fun Double.times(target: Array<Int>): Array<Int> {
    return target.map { (this * it).toInt() }.toTypedArray()
}

private operator fun Double.times(target: Array<Double>): Array<Double> {
    return target.map { this * it }.toTypedArray()
}

private operator fun Array<Int>.minus(target: Array<Int>): Array<Int> {
    val indexes = 0..<min(this.size, target.size)
    return indexes.map { index -> this[index] - target[index] }.toTypedArray()
}
