package graph

import java.util.UUID

data class Edge(
    val id: UUID = UUID.randomUUID(),
    val star
)
