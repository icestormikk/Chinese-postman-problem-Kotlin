package common

import genetic_algorithms.GeneticAlgorithmConfiguration
import kotlinx.serialization.Serializable
import particles_swarm.ParticlesSwarmConfiguration
import simulated_annealing.SimulatedAnnealingConfiguration

@Serializable
data class Configuration(
    val type: AlgorithmType,
    val genetic: GeneticAlgorithmConfiguration? = null,
    val particleSwarm: ParticlesSwarmConfiguration? = null,
    val annealing: SimulatedAnnealingConfiguration? = null,
)

