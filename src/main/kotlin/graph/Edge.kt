package graph

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Edge<T>(
    val source: Node,
    val destination: Node,
    val weight: T,
    val type: EdgeType,
    val id: String = UUID.randomUUID().toString()
)

