package graph

import kotlinx.serialization.Serializable

@Serializable
data class GraphDao(
    val nodes: List<Node>,
    val edges: List<Edge<Double>>
)