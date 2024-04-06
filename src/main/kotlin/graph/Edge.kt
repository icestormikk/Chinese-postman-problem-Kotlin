package graph

import common.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Edge<T>(
    val source: Node,
    val destination: Node,
    val weight: T,
    val type: EdgeType
) : Identifiable()

