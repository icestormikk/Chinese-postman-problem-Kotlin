package genetic_algorithms

import genetic_algorithms.operators.*
import kotlinx.serialization.Serializable

@Serializable
data class GeneticAlgorithmConfiguration(
    val iterationsCount: Int,
    val populationSize: Int,
    val startNodeId: String? = null,
    val parents: ParentsConfiguration,
    val recombinationType: RecombinationMethods.Types,
    val mutation: MutationConfiguration,
    val newPopulation: PopulationSelectionConfiguration,
)

@Serializable
data class ParentsConfiguration(
    val selection: SelectionMethods.Types,
    val chooser: ParentSelectionMethods.Types
)

@Serializable
data class PopulationSelectionConfiguration(
    val type: NewPopulationMethods.Types,
    val rate: Double = 1.0
)

@Serializable
data class MutationConfiguration(
    val type: MutationMethods.Types,
    val percent: Double = 0.5
)