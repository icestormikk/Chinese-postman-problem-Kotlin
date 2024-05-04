package utils.validators

import common.AlgorithmType
import common.Configuration
import genetic_algorithms.GeneticAlgorithmConfiguration
import graph.Graph
import particles_swarm.ParticlesSwarmConfiguration
import utils.helpers.LoggingHelper

class ConfigurationValidator {
    private val logger = LoggingHelper().getLogger(ConfigurationValidator::class.simpleName.toString())

    fun <T> validateConfiguration(configuration: Configuration, graph: Graph<T>) {
        when (configuration.type) {
            AlgorithmType.GENETIC -> {
                requireNotNull (configuration.genetic) {
                    "A genetic algorithm was selected, but the configuration for it was not transmitted"
                }
                try {
                    validateGeneticConfiguration(configuration.genetic, graph)
                } catch (e: Exception) {
                    logger.error { e.message }
                    throw IllegalArgumentException("Error during configuration validation for the genetic algorithm (${e.message})")
                }
            }
            AlgorithmType.PARTICLES_SWARM -> {
                requireNotNull (configuration.particleSwarm) {
                    "The particles swarm method was selected, but the configuration for it was not transmitted"
                }
                try {
                    validateParticleSwarmConfiguration(configuration.particleSwarm, graph)
                } catch (e: Exception) {
                    logger.error { e.message }
                    throw IllegalArgumentException("Error during configuration validation for the particles swarm algorithm (${e.message})")
                }
            }
            AlgorithmType.ANNEALING -> {
                throw NotImplementedError()
            }
        }
    }

    private fun <T> validateGeneticConfiguration(configuration: GeneticAlgorithmConfiguration, graph: Graph<T>) {
        with (configuration) {
            require (iterationsCount > 0) { "The number of iterations must be strictly positive" }
            require(populationSize > 0) { "The size of the created populations must be strictly greater than zero" }
            require(mutation.rate > 0.0) { "The probability of mutation must be greater than zero" }
            if (startNodeId != null) {
                require(graph.nodes.any { it.id == startNodeId }) {
                    "The vertex with the id ${startNodeId}, which was planned as the starting vertex, was not found in the transmitted graph"
                }
            }
        }
    }

    private fun <T> validateParticleSwarmConfiguration(configuration: ParticlesSwarmConfiguration, graph: Graph<T>) {
        with (configuration) {
            require (iterationsCount > 0) { "The number of iterations must be strictly positive" }
            require (swarmSize > 0) { "The particle swarm size must be strictly greater than 0" }
            if (startNodeId != null) {
                require(graph.nodes.any { it.id == startNodeId }) {
                    "The vertex with the id ${startNodeId}, which was planned as the starting vertex, was not found in the transmitted graph"
                }
            }
        }
    }
}