package particles_swarm

import graph.Graph
import utils.helpers.LoggingHelper

class ParticleSwarm {
    private val logger = LoggingHelper().getLogger(ParticleSwarm::class.java.simpleName)

    class GraphSwarm(
        size: Int,
        currentVelocityRatio: Double,
        localVelocityRatio: Double,
        globalVelocityRatio: Double,
        graph: Graph<Double>
    ): Swarm(size, currentVelocityRatio, localVelocityRatio, globalVelocityRatio, graph) {
        override fun onFitness(position: Array<Int>): Double {
            return try {
                val path = position.map { index -> graph.edges[index] }.toTypedArray()
                graph.calculateTotalLengthOf(path)
            } catch (ex: Exception) {
                Double.NEGATIVE_INFINITY
            }
        }

        override fun getPenalty(position: Array<Int>, ratio: Double): Double {
            val penalty1 = position.count { index -> index < 0 } * ratio
            val penalty2 = position.count { index -> index > graph.edges.lastIndex } * ratio

            return penalty1 + penalty2
        }

    }

    fun start(graph: Graph<Double>, configuration: ParticlesSwarmConfiguration): Array<Int> {
        logger.info { "Launching the particle swarm method" }
        val (iterationsCount, swarmSize, currentVelocityRatio, localVelocityRatio, globalVelocityRatio) = configuration

        val swarm = GraphSwarm(swarmSize, currentVelocityRatio, localVelocityRatio, globalVelocityRatio, graph)
        logger.info { "Create a \"swarm\" of $swarmSize \"particles\" (id: ${swarm.id})" }

        for (i in 0..iterationsCount) {
            swarm.nextIteration()
        }

        logger.info { "The particle swarm method has completed its work" }
        return swarm.globalBestPosition!!
    }
}