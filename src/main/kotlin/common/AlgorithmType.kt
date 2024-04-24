package common

import kotlinx.serialization.Serializable

@Serializable
enum class AlgorithmType {
    GENETIC,
    PARTICLES_SWARM,
    ANNEALING,
}