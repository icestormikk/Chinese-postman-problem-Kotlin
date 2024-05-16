package common

import genetic_algorithms.GeneticAlgorithmConfiguration
import kotlinx.serialization.Serializable
import particles_swarm.ParticlesSwarmConfiguration
import simulated_annealing.SimulatedAnnealingConfiguration

/**
 * Класс для хранения полученных от пользотеля данных о желаемом алгоритме и его начальных значениях
 * @property type Тип алгоритма, по которому будет рассчитываться оптимальный путь
 * @property genetic Конфигурация для генетического алгоритма
 * @property particleSwarm Конфигурация для алгоритма роя частиц
 * @property annealing Конфигурация для метода отжига
 */
@Serializable
data class Configuration(
    val type: AlgorithmType,
    val genetic: GeneticAlgorithmConfiguration? = null,
    val particleSwarm: ParticlesSwarmConfiguration? = null,
    val annealing: SimulatedAnnealingConfiguration? = null,
)

