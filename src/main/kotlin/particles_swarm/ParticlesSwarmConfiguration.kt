package particles_swarm

import kotlinx.serialization.Serializable

// Настройки для алгоритма оптимизации роем частиц
@Serializable
@Deprecated("The algorithm in which this class is used is recognized as extremely inefficient")
data class ParticlesSwarmConfiguration(
    // Количество итераций алгоритма
    val iterationsCount: Int,
    // Размер роя (количество частиц в одном рое)
    val swarmSize: Int,
    // Коэффициент k
    val currentVelocityRatio: Double = 0.1,
    // Весовой коэффициент для лучшего локального решения
    val localVelocityRatio: Double = 1.0,
    // Весовой коэффициент для лучшего глобального решения
    val globalVelocityRatio: Double = 5.0,
    // Уникальный идентификатор начальной вершины
    val startNodeId: String? = null,
)