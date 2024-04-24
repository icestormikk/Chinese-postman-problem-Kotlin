package particles_swarm

import common.Identifiable
import graph.Graph
import graph.Node
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.times

data class Particle<T>(
    val swarm: Swarm,
    val graph: Graph<T>,
    val startNode: Node = graph.nodes.random()
) : Identifiable() {
    var position = initializePosition()
    var velocity = initializeVelocity()
    var localBestPosition = position
    var localBestFitnessValue = swarm.calculateFitness(position)

    private fun initializePosition(): Array<Int> {
        return graph.getRandomPath(startNode).map { graph.edges.indexOf(it) }.toTypedArray()
    }
    private fun initializeVelocity(): Array<Int> {
        return position.map { if (Math.random() > 0.5) 1 else 0 }.toTypedArray()
    }

    fun nextIteration(swarm: Swarm) {
        if (swarm.globalBestPosition == null) return

        val randomCurrentBestPosition = Math.random()
        val randomGlobalBestPosition = Math.random()
        val velocityRatio = swarm.localVelocityRatio - swarm.globalVelocityRatio
        val commonRatio = (2.0 * swarm.currentVelocityRatio
                / abs(2.0 - velocityRatio - sqrt(velocityRatio.pow(2) - 4.0*velocityRatio)))

        val newVelocity1 = commonRatio * velocity
        val newVelocity2 = commonRatio *
                swarm.localVelocityRatio *
                randomCurrentBestPosition * (localBestPosition - position)
        val newVelocity3 = commonRatio *
                swarm.globalVelocityRatio *
                randomGlobalBestPosition * (swarm.globalBestPosition!! - position)

        velocity = newVelocity1 + newVelocity2 + newVelocity3
        position += velocity

        val fitness = swarm.calculateFitness(position)
        if (fitness < localBestFitnessValue) {
            localBestFitnessValue = fitness
            localBestPosition = position
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Particle<*>

        if (swarm != other.swarm) return false
        if (!position.contentEquals(other.position)) return false
        if (!velocity.contentEquals(other.velocity)) return false
        if (!localBestPosition.contentEquals(other.localBestPosition)) return false
        return localBestFitnessValue == other.localBestFitnessValue
    }

    override fun hashCode(): Int {
        var result = swarm.hashCode()
        result = 31 * result + position.contentHashCode()
        result = 31 * result + velocity.contentHashCode()
        result = 31 * result + localBestPosition.contentHashCode()
        result = 31 * result + localBestFitnessValue.hashCode()
        return result
    }
}

private operator fun Array<Double>.plus(target: Array<Int>): Array<Int> {
    val indexes = 0..<min(this.size, target.size)
    return indexes.map { index -> (this[index] + target[index]).toInt() }.toTypedArray()
}

private operator fun Array<Int>.plus(target: Array<Double>): Array<Int> {
    val indexes = 0..<min(this.size, target.size)
    return indexes.map { index -> (this[index] + target[index]).toInt() }.toTypedArray()
}

private operator fun Array<Double>.plus(target: Array<Double>): Array<Double> {
    val indexes = 0..<min(this.size, target.size)
    return indexes.map { index -> this[index] + target[index] }.toTypedArray()
}

private operator fun Double.times(target: Array<Int>): Array<Int> {
    return target.map { (this * it).toInt() }.toTypedArray()
}

private operator fun Double.times(target: Array<Double>): Array<Double> {
    return target.map { this * it }.toTypedArray()
}

private operator fun Array<Int>.minus(target: Array<Int>): Array<Int> {
    val indexes = 0..<min(this.size, target.size)
    return indexes.map { index -> this[index] - target[index] }.toTypedArray()
}
