package common

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
abstract class Identifiable {
    val id: String = UUID.randomUUID().toString()
}