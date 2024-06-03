package common

import kotlinx.serialization.Serializable

/**
 * Список доступных алгоритмов
 */
@Serializable
enum class AlgorithmType {
    // Генетический алгоритм
    GENETIC,
    // Метод имитации муравьиной колонии
    ANT_COLONY
}