package common

import kotlinx.serialization.Serializable

/**
 * Список доступных алгоритмов
 */
@Serializable
enum class AlgorithmType {
    // Генетический алгоритм
    GENETIC,
    // Алгоритм роя частиц
    PARTICLES_SWARM,
    // Метод отжига
    ANNEALING,
    // Метод имитации муравьиной колонии
    ANT_COLONY
}