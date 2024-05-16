package simulated_annealing

import graph.Edge
import graph.Graph
import graph.Node
import utils.helpers.LoggingHelper
import kotlin.math.exp

// Класс для запуска метода отжига
class SimulatedAnnealing {
    private val logger = LoggingHelper().getLogger(SimulatedAnnealing::class.java.simpleName)

    fun start(
        // граф, для которого ищется решение
        graph: Graph<Double>,
        // конфигурация запуска с основыми настройками алгоритма
        configuration: SimulatedAnnealingConfiguration,
        // функция вычисления энергии для конкретного состояния системы
        onEnergy: (state: Array<Edge<Double>>) -> Double,
        // функция уменьшения температуры
        onTemperature: (prevTemperature: Double, iterationIndex: Int, coefficient: Double) -> Double,
        // функция перехода системы в новое состояние
        onNewState: (state: Array<Edge<Double>>) -> Array<Edge<Double>>,
        // начальная вершина
        startNode: Node? = null
    ): Array<Edge<Double>> {
        val (iterationCount, minTemperature, maxTemperature, temperatureDecreasingCoefficient) = configuration

        require(minTemperature < maxTemperature) { "The minimum temperature must be strictly less than the maximum" }

        // начальное состояние (случайный путь в графе) и начальное значение энергии (длина пути)
        var state = (if (startNode == null) graph.getRandomPath() else graph.getRandomPath(startNode)).toTypedArray()
        var currentEnergy = onEnergy(state)
        var currentTemperature = maxTemperature

        var index = 0
        while (currentTemperature > minTemperature) {
            // создание нового состояния (состояние-сосед, в которое возможен переход)
            val candidateState = onNewState(state)
            // энергия нового состояния
            val candidateEnergy = onEnergy(candidateState)

            // если энергия нового состояния меньше, чем энергия предыдущего, гарантированно обновляем состояние
            if (candidateEnergy < currentEnergy) {
                currentEnergy = candidateEnergy
                state = candidateState
            } else {
                // иначе считаем вероятность перехода
                val probability = onProbability(candidateEnergy - currentEnergy, currentTemperature)
                if (isTransition(probability)) {
                    currentEnergy = candidateEnergy
                    state = candidateState
                }
            }

            // уменьшаем температуру
            currentTemperature = onTemperature(currentTemperature, index++, temperatureDecreasingCoefficient)
            logger.info { "Temperature has been updated: $currentTemperature" }
        }

        return state
    }

    // вероятность перехода системы в новое состояние
    private fun onProbability(energyDiff: Double, temperature: Double): Double {
        return exp(-energyDiff / temperature)
    }

    // проверка, возможен ли переход
    private fun isTransition(probability: Double): Boolean {
        require(probability in 0.0..1.0) { "The probability of transition should be in the range from 0 to 1" }
        return Math.random() < probability
    }
}