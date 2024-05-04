package simulated_annealing

import graph.Edge
import graph.Graph
import graph.Node
import utils.helpers.LoggingHelper
import kotlin.math.exp

class SimulatedAnnealing {
    private val logger = LoggingHelper().getLogger(SimulatedAnnealing::class.java.simpleName)

    fun start(
        graph: Graph<Double>,
        minTemperature: Double,
        maxTemperature: Double,
        onEnergy: (state: Array<Edge<Double>>) -> Double,
        onNewState: (state: Array<Edge<Double>>) -> Array<Edge<Double>>,
        startNode: Node? = null
    ): Array<Edge<Double>> {
        require(minTemperature < maxTemperature) { "The minimum temperature must be strictly less than the maximum" }

        var state = (if (startNode == null) graph.getRandomPath() else graph.getRandomPath(startNode)).toTypedArray()
        var currentEnergy = onEnergy(state)
        var currentTemperature = maxTemperature

        for (i in 1..10000) {
            val candidateState = onNewState(state)
            val candidateEnergy = onEnergy(candidateState)

            if (candidateEnergy < currentEnergy) {
                currentEnergy = candidateEnergy
                state = candidateState
//                logger.info { "Current energy has been updated (1): $currentEnergy" }
            } else {
                val probability = onProbability(candidateEnergy - currentEnergy, currentTemperature)
                if (isTransition(probability)) {
                    currentEnergy = candidateEnergy
                    state = candidateState
                }
            }

            currentTemperature = decreaseTemperature(maxTemperature, i)
            logger.info { "Temperature has been updated: $currentTemperature" }
            if (currentTemperature <= minTemperature) {
                break
            }
        }

        return state
    }

    private fun onProbability(energyDiff: Double, temperature: Double): Double {
        return exp(-energyDiff / temperature)
    }

    private fun decreaseTemperature(initialTemperature: Double,  iterationIndex: Int): Double {
        return initialTemperature * 0.9 / iterationIndex
    }

    private fun isTransition(probability: Double): Boolean {
        require(probability in 0.0..1.0) { "The probability of transition should be in the range from 0 to 1" }
        return Math.random() < probability
    }
}