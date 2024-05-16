package common

import kotlinx.serialization.Serializable

/**
 * Формат ответа программы
 * @property path Полученный в результате работы алгоритма оптимальный путь. Представлен в виде набора индентификаторов рёбер
 * @property length Длина полученного пути
 * @property executionTimeMs Время работы программы (в мс)
 */
@Serializable
data class Response(
    val path: List<String>,
    val length: Double,
    val executionTimeMs: Long
)