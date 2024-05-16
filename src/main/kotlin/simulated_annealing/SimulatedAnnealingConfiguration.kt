package simulated_annealing

import kotlinx.serialization.Serializable

// Настройки для алгоритма симуляции отжига
@Serializable
data class SimulatedAnnealingConfiguration(
    // Количество итераций алгоритма
    val iterationCount: Int,
    // Минимальная температура (при достижении её, алгоритм прекращает свою работу)
    val minTemperature: Double,
    // Максимальная температура (с неё алгоритм начинает свою работу)
    val maxTemperature: Double,
    // Коэффициент понижения температуры на каждом шаге (определяет скорость уменьшения темперетуры)
    val temperatureDecreasingCoefficient: Double,
    // Уникальный идентификатор начальной вершины
    val selectedNodeId: String? = null
)