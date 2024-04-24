package particles_swarm

import common.Identifiable
import graph.Graph
import utils.helpers.LoggingHelper

abstract class Swarm(
    open val size: Int,
    open val currentVelocityRatio: Double,
    open val localVelocityRatio: Double,
    open val globalVelocityRatio: Double,
    val graph: Graph<Double>
) : Identifiable() {
    var globalBestFitness: Double? = null
    var globalBestPosition: Array<Int>? = null
    val particles by lazy { initialize() }

    companion object {
        private val logger = LoggingHelper.getLogger("SWARM_LOGGER")
    }

    abstract fun onFitness(position: Array<Int>): Double
    abstract fun getPenalty(position: Array<Int>, ratio: Double): Double

    private fun initialize(): List<Particle<Double>> {
        return (0..<size).map { Particle(this, graph) }
    }

    fun calculateFitness(position: Array<Int>): Double {
        val currentFitness = onFitness(position) + getPenalty(position, 10000.0)

        if (globalBestFitness == null || currentFitness < globalBestFitness!!) {
            logger.info { "Updated the global optimal position of the swarm with id $id: $globalBestFitness" }
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
