package genetic_algorithms.algorithm

import genetic_algorithms.operators.*
import kotlinx.serialization.Serializable

// Конфигурация для генетического алгоритма
@Serializable
data class GeneticAlgorithmConfiguration(
    // Общее количество итераций алгоритма
    val iterationsCount: Int,
    // Размер популяции
    val populationSize: Int,
    // Настройки выбора родителей
    val parents: ParentsConfiguration,
    // Настройки для обмена генов
    val recombination: RecombinationConfiguration,
    // Настройки для мутации генов особей
    val mutation: MutationConfiguration,
    // Настройки создания нового поколения мутации
    val newPopulation: PopulationSelectionConfiguration,
)

@Serializable
data class RecombinationConfiguration(
    val type: RecombinationMethods.Types? = null,
    val rate: Double = 1.0
)

@Serializable
data class ParentsConfiguration(
    // Метод составления промежуточной популяции
    val selection: SelectionMethods.Types? = null,
    // Метод выбора родителей
    val chooser: ParentSelectionMethods.Types? = null
)

@Serializable
data class PopulationSelectionConfiguration(
    // Метод составления новой популяции
    val type: NewPopulationMethods.Types? = null,
    // Процент от общего числа особей, которые проходят в следующее поколение
    val rate: Double = 1.0
)

@Serializable
data class MutationConfiguration(
    // Тип мутации
    val type: MutationMethods.Types? = null,
    // Вероятность мутации
    val rate: Double = 0.5
)