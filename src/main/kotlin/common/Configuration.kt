package common

import genetic_algorithms.GeneticAlgorithmConfiguration
import kotlinx.serialization.Serializable
import particles_swarm.ParticlesSwarmConfiguration

@Serializable
data class Configuration(
    val type: AlgorithmType,
    val genetic: GeneticAlgorithmConfiguration? = null,
    val particleSwarm: ParticlesSwarmConfiguration? = null
)

