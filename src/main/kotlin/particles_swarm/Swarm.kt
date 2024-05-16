package particles_swarm

import common.Identifiable
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
abstract class Swarm(
    open val size: Int,
    open val currentVelocityRatio: Double,
    open val localVelocityRatio: Double,
    open val globalVelocityRatio: Double,
    val graph: Graph<Double>
) : Identifiable() {
    // значение функции в глобально лучшей позиции
    var globalBestFitness: Double? = null
    // лучшая позиция для всего роя
    var globalBestPosition: Array<Int>? = null
    // список частиц в рое
    val particles by lazy { initialize() }

    companion object {
        private val logger = LoggingHelper().getLogger("SWARM_LOGGER")
    }

    // Абстрактная функция пригодности частицы
    abstract fun onFitness(position: Array<Int>): Double
    // Абстрактная функция расчёта штрафа для частицы, в случае если какая-то из координат частицы выходит за границы поиска
    abstract fun getPenalty(position: Array<Int>, ratio: Double): Double

    // создание набора частиц
    private fun initialize(): List<Particle<Double>> {
        return (0..<size).map { Particle(this, graph) }
    }

    // функция рассчёта пригодности частицы с учётом штрафа и обновление глобального лучшего положения
    fun calculateFitness(position: Array<Int>): Double {
        val currentFitness = onFitness(position) + getPenalty(position, 10000.0)

        if (globalBestFitness == null || currentFitness < globalBestFitness!!) {
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
