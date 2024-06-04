package utils.validators

import ant_colony.AntColonyAlgorithmConfiguration
import common.AlgorithmType
import common.Configuration
import genetic_algorithms.algorithm.GeneticAlgorithmConfiguration
import graph.Edge
import graph.Graph
import utils.helpers.LoggingHelper

class ConfigurationValidator {
    private val logger = LoggingHelper().getLogger(ConfigurationValidator::class.simpleName.toString())

    fun <T> validateConfiguration(configuration: Configuration, graph: Graph<T, Edge<T>>) {

        if (configuration.startNodeId != null) {
            require(graph.nodes.any { it.id == configuration.startNodeId }) {
                "The vertex with the id ${configuration.startNodeId}, which was planned as the starting vertex, was not found in the transmitted graph"
            }
        }

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
//            AlgorithmType.PARTICLES_SWARM -> {
//                requireNotNull (configuration.particleSwarm) {
//                    "The particles swarm method was selected, but the configuration for it was not transmitted"
//                }
//                try {
//                    validateParticleSwarmConfiguration(configuration.particleSwarm, graph)
//                } catch (e: Exception) {
//                    logger.error { e.message }
//                    throw IllegalArgumentException("Error during configuration validation for the particles swarm algorithm (${e.message})")
//                }
//            }
//            AlgorithmType.ANNEALING -> {
//                requireNotNull(configuration.annealing) {
//                    "The simulated annealing method was selected, but the configuration for it was not transmitted"
//                }
//                try {
//                    validateSimulatedAnnealingConfiguration(configuration.annealing)
//                } catch (e: Exception) {
//                    logger.error { e.message }
//                    throw IllegalArgumentException("Error during configuration validation for the simulated annealing algorithm (${e.message})")
//                }
//            }
            AlgorithmType.ANT_COLONY -> {
                requireNotNull(configuration.antColony) {
                    "The ant colony method was selected, but the configuration for it was not transmitted"
                }
                try {
                    validateAntColonyConfiguration(configuration.antColony, graph)
                } catch (e: Exception) {
                    logger.error { e.message }
                    throw IllegalArgumentException("Error during configuration validation for the any colony algorithm (${e.message})")
                }
            }
        }
    }

    private fun <T> validateGeneticConfiguration(configuration: GeneticAlgorithmConfiguration, graph: Graph<T, Edge<T>>) {
        with (configuration) {
            require (iterationsCount > 0) { "The number of iterations must be strictly positive" }
            require(populationSize > 0) { "The size of the created populations must be strictly greater than zero" }
            require(recombination.rate >= 0.0) { "The probability of recombination must be greater than zero" }
            require(mutation.rate >= 0.0) { "The probability of mutation must be greater than zero" }
        }
    }

//    private fun <T> validateParticleSwarmConfiguration(configuration: ParticlesSwarmConfiguration, graph: Graph<T, Edge<T>>) {
//        with (configuration) {
//            require (iterationsCount > 0) { "The number of iterations must be strictly positive" }
//            require (swarmSize > 0) { "The particle swarm size must be strictly greater than 0" }
//            if (startNodeId != null) {
//                require(graph.nodes.any { it.id == startNodeId }) {
//                    "The vertex with the id ${startNodeId}, which was planned as the starting vertex, was not found in the transmitted graph"
//                }
//            }
//        }
//    }

//    private fun validateSimulatedAnnealingConfiguration(configuration: SimulatedAnnealingConfiguration) {
//        with (configuration) {
//            require (minTemperature < maxTemperature) { "The minimum temperature must not exceed the minimum" }
//        }
//    }

    private fun <T> validateAntColonyConfiguration(configuration: AntColonyAlgorithmConfiguration, graph: Graph<T, Edge<T>>) {
        with (configuration) {
            require (iterationCount > 0) { "The number of iterations must be strictly positive" }
            require (antCount > 0) { "The number of ants must be strictly positive" }
            require (proximityCoefficient > 0) { "The proximity coefficient must be strictly positive" }
            require (startPheromoneValue >= 0) { "The initial amount of pheromones on the branches should not be negative" }
            require (remainingPheromoneRate in 0.0..<1.0) { "The percentage of pheromone evaporation should be in the range [0, 1)" }
        }
    }
}