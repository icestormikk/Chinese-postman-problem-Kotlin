package particles_swarm

import common.Identifiable
import graph.Edge
import graph.Graph
import utils.helpers.LoggingHelper

/**
 * Абстрактный класс, описывающий рой частиц
 * @property size Количество частиц в рое
 * @property currentVelocityRatio Параметр k, участвующий в рассчёте скорости
 * @property localVelocityRatio Весовой коэффициент для лучшего локального решения
 * @property globalVelocityRatio Весовой коэффициент для лучшего глобального решения
 * @property graph Граф, на котором происходит решение задачи
 */
@Deprecated("The algorithm in which this class is used is recognized as extremely inefficient")
abstract class Swarm<T: Comparable<T>>(
    open val size: Int,
    open val currentVelocityRatio: Double,
    open val localVelocityRatio: Double,
    open val globalVelocityRatio: Double,
    val graph: Graph<T, Edge<T>>
) : Identifiable() {
    // значение функции в глобально лучшей позиции
    var globalBestFitness: Double? = null
    // лучшая позиция для всего роя
    var globalBestPosition: List<Edge<T>>? = null
    // список частиц в рое
    val particles by lazy { initialize() }

    companion object {
        private val logger = LoggingHelper().getLogger("SWARM_LOGGER")
    }

    // Абстрактная функция пригодности частицы
    abstract fun onFitness(position: List<Edge<T>>): Double
    // Абстрактная функция расчёта штрафа для частицы, в случае если какая-то из координат частицы выходит за границы поиска
    abstract fun getPenalty(position: List<Edge<T>>, ratio: Double): Double

    // создание набора частиц
    private fun initialize(): List<Particle<T>> {
        return (0..<size).map { Particle(this, graph) }
    }

    // функция рассчёта пригодности частицы с учётом штрафа и обновление глобального лучшего положения
    fun calculateFitness(position: List<Edge<T>>): Double {
        val currentFitness = onFitness(position) + getPenalty(position, 10000.0)

        if (globalBestFitness == null || currentFitness > globalBestFitness!!) {
            logger.info { "Updated the global optimal position of the swarm with id $id: $globalBestFitness" }
            globalBestFitness = currentFitness
            globalBestPosition = position
        }

        return currentFitness
    }

    // Обновление положения частиц
    fun nextIteration() {
        for (particle in particles) {
            particle.nextIteration(this)
        }
    }
}
