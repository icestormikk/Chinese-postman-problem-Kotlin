package graph

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Класс, содержащий описание вершины графа
 * @property label Название вершины
 * @property id Уникальный идентификационный номер вершины в графе
 */
@Serializable
data class Node @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val label: String = "Node",
    val id: String
)
