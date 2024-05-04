package simulated_annealing

import kotlinx.serialization.Serializable

@Serializable
data class SimulatedAnnealingConfiguration(
    val minTemperature: Double,
    val maxTemperature: Double,
    val temperatureIncreasingCoefficient: Double
)