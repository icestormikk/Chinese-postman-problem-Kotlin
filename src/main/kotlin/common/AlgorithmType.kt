package common

import kotlinx.serialization.Serializable

@Serializable
enum class AlgorithmType {
    GENETIC,
    PARTICLE_SWARM,
    ANNEALING,
}