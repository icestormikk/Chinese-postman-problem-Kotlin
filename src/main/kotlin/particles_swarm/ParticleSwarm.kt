package particles_swarm

import graph.Edge
import graph.Graph
import utils.helpers.LoggingHelper

class ParticleSwarm {
    private val logger = LoggingHelper().getLogger(ParticleSwarm::class.java.simpleName)

    class GraphSwarm<T>(
        size: Int,
        currentVelocityRatio: Double,
        localVelocityRatio: Double,
        globalVelocityRatio: Double,
        graph: Graph<T, Edge<T>>
    ): Swarm<T>(size, currentVelocityRatio, localVelocityRatio, globalVelocityRatio, graph) {
        override fun onFitness(position: List<Edge<T>>): Double {
            return try {
                graph.calculateTotalLengthOf(position.toTypedArray())
            } catch (ex: Exception) {
                Double.MIN_VALUE
            }
        }

        override fun getPenalty(position: List<Edge<T>>, ratio: Double): Double {
            return position.sumOf { if (!position.contains(it)) ratio else 0.0 }
        }
    }

    fun start(graph: Graph<Double, Edge<Double>>, configuration: ParticlesSwarmConfiguration): List<Edge<Double>> {
        logger.info { "Launching the particle swarm method" }
        val (iterationsCount, swarmSize, currentVelocityRatio, localVelocityRatio, globalVelocityRatio) = configuration

        val swarm = GraphSwarm(swarmSize, currentVelocityRatio, localVelocityRatio, globalVelocityRatio, graph)
        logger.info { "Create a \"swarm\" of $swarmSize \"particles\" (id: ${swarm.id})" }

        for (i in 1..iterationsCount) {
            swarm.nextIteration()
        }

        logger.info { "The particle swarm method has completed its work" }
        val best = swarm.globalBestPosition ?: throw IllegalStateException("The best position of the swarm is null")
        return best
    }
}