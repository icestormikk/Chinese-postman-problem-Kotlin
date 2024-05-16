package graph

import kotlinx.serialization.Serializable

/**
 * Класс, содержащий описание каждого из рёбер в графе
 * @param T Тип веса ребра (целое число, вещественное число и тд)
 * @property source Вершина, из которой выходит ребро
 * @property destination Вершина, являющаяся пунктом назначения для ребра
 * @property weight Вес ребра
 * @property id Уникальный идентификационный номер ребра
 * @constructor Create empty Edge
 */
@Serializable
data class Edge<T>(
    val source: Node,
    val destination: Node,
    val weight: T,
    val id: String
)

