package particles_swarm

import common.Identifiable

abstract class Swarm(
    open val size: Int,
    open val minimumValues: Array<Double>,
    open val maximumValues: Array<Double>,
    open val currentVelocityRatio: Double,
    open val localVelocityRatio: Double,
    open val globalVelocityRatio: Double,
) : Identifiable() {
    var globalBestFitness: Double? = null
    var globalBestPosition: Array<Double>? = null
    val particles by lazy { initialize() }

    abstract fun onFitness(position: Array<Double>): Double
    abstract fun getPenalty(position: Array<Double>, ratio: Double): Double

    private fun initialize(): List<Particle> {
        return (0..<size).map { Particle(swarm = this) }
    }

    fun calculateFitness(position: Array<Double>): Double {
        assert(position.size == minimumValues.size)
        val currentFitness = onFitness(position) + getPenalty(position, 10000.0)

        if (globalBestFitness == null || currentFitness < globalBestFitness!!) {
            globalBestFitness = currentFitness
            globalBestPosition = position
        }

        return currentFitness
    }

    fun nextIteration() {
        for (particle in particles) {
            particle.nextIteration(this)
        }
    }
}
