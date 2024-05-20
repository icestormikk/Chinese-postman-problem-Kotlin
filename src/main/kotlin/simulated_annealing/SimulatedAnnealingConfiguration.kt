package simulated_annealing

import kotlinx.serialization.Serializable

// Настройки для алгоритма симуляции отжига
@Serializable
@Deprecated("The algorithm in which this class is used is recognized as extremely inefficient")
data class SimulatedAnnealingConfiguration(
    // Минимальная температура (при достижении её, алгоритм прекращает свою работу)
    val minTemperature: Double,
    // Максимальная температура (с неё алгоритм начинает свою работу)
    val maxTemperature: Double,
    // Коэффициент понижения температуры на каждом шаге (определяет скорость уменьшения темперетуры)
    val temperatureDecreasingCoefficient: Double,
    // Уникальный идентификатор начальной вершины
    val selectedNodeId: String? = null
)