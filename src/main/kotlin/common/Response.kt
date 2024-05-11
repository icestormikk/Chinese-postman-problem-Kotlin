package common

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val path: List<String>,
    val length: Double,
    val executionTimeMs: Long
)