package common

import ant_colony.AntColonyAlgorithmConfiguration
import genetic_algorithms.algorithm.GeneticAlgorithmConfiguration
import kotlinx.serialization.Serializable

/**
 * Класс для хранения полученных от пользотеля данных о желаемом алгоритме и его начальных значениях
 * @property type Тип алгоритма, по которому будет рассчитываться оптимальный путь
 * @property maxLength Максимальная длина искомого пути
 * @property genetic Конфигурация для генетического алгоритма
 * @property antColony Конфигурация для метода имитации муравьиной колонии
 */
@Serializable
data class Configuration(
    val type: AlgorithmType,
    val maxLength: Double,
    val genetic: GeneticAlgorithmConfiguration? = null,
    val antColony: AntColonyAlgorithmConfiguration? = null
)

