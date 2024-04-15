package particles_swarm

import common.Identifiable
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Particle(
    val swarm: Swarm
) : Identifiable() {
    var position = initializePosition(swarm)
    var velocity = initializeVelocity(swarm)
    var localBestPosition = position
    var localBestFitnessValue = swarm.calculateFitness(position)

    private fun initializePosition(swarm: Swarm): Array<Double> {
        return swarm.minimumValues.mapIndexed { index, minimum ->
            val maximum = swarm.maximumValues[index]
            minimum + Math.random() * (maximum - minimum)
        }.toTypedArray()
    }

    private fun initializeVelocity(swarm: Swarm): Array<Double> {
        with (swarm) {
            assert(minimumValues.size == position.size)
            assert(maximumValues.size == position.size)

            return minimumValues.mapIndexed { index, minimum ->
                val maximum = maximumValues[index]
                minimum + Math.random() * (maximum - minimum)
            }.toTypedArray()
        }
    }

    fun nextIteration(swarm: Swarm) {
        if (swarm.globalBestPosition == null) return

        val randomCurrentBestPosition = Math.random()
        val randomGlobalBestPosition = Math.random()
        val velocityRatio = swarm.localVelocityRatio - swarm.globalVelocityRatio
        val commonRatio = (2.0 * swarm.currentVelocityRatio
                / abs(2.0 - velocityRatio - sqrt(velocityRatio.pow(2) - 4.0*velocityRatio)))

        val newVelocity1 = velocity * commonRatio
        val newVelocity2 = commonRatio *
                swarm.localVelocityRatio *
                (localBestPosition - position) * randomCurrentBestPosition
        val newVelocity3 = commonRatio *
                swarm.globalVelocityRatio *
                (swarm.globalBestPosition!! - position) * randomGlobalBestPosition

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

        other as Particle

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

private operator fun Array<Double>.minus(target: Array<Double>): Array<Double> {
    return this
        .mapIndexed { index, element -> element - target[index] }
        .toTypedArray()
}

private operator fun Array<Double>.times(i: Double): Array<Double> {
    return this.map { it * i }.toTypedArray()
}

private operator fun Array<Double>.plus(second: Array<Double>): Array<Double> {
    return this.mapIndexed { index, first -> first + second[index] }.toTypedArray()
}

private operator fun Double.times(target: Array<Double>): Array<Double> {
    return target * this
}
