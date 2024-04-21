package common

import genetic_algorithms.GeneticAlgorithmConfiguration
import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val type: AlgorithmType,
    val genetic: GeneticAlgorithmConfiguration? = null,
    val particleSwarm: ParticleSwarmConfiguration? = null
)

@Serializable
data class ParticleSwarmConfiguration(
    val iterationsCount: Int,
    val swarmSize: Int,
)