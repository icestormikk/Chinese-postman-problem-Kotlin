package particles_swarm

import kotlinx.serialization.Serializable

@Serializable
data class ParticlesSwarmConfiguration(
    val iterationsCount: Int,
    val swarmSize: Int,
    val currentVelocityRatio: Double = 0.1,
    val localVelocityRatio: Double = 1.0,
    val globalVelocityRatio: Double = 5.0,
    val startNodeId: String? = null,
)