package genetic_algorithms

import genetic_algorithms.operators.MutationMethods
import genetic_algorithms.operators.NewPopulationMethods
import genetic_algorithms.operators.ParentSelectionMethods
import genetic_algorithms.operators.RecombinationMethods
import kotlinx.serialization.Serializable

@Serializable
data class GeneticAlgorithmConfiguration(
    val iterationsCount: Int,
    val populationSelectionType: NewPopulationMethods.Types,
    val parentSelectionType: ParentSelectionMethods.Types,
    val recombinationType: RecombinationConfiguration,
    val mutationType: MutationConfiguration,
)

@Serializable
data class RecombinationConfiguration(
    val type: RecombinationMethods.Types,
    val percent: Double = 1.0
)

@Serializable
data class MutationConfiguration(
    val type: MutationMethods.Types,
    val percent: Double = 0.5
)