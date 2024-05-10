package graph

import kotlinx.serialization.Serializable

@Serializable
data class Edge<T>(
    val source: Node,
    val destination: Node,
    val weight: T,
    val id: String
)

