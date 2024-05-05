package graph

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Node @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val label: String = "Node",
    val id: String = UUID.randomUUID().toString()
)
