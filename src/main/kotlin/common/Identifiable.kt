package common

import kotlinx.serialization.Serializable
import utils.serializers.UUIDSerializer
import java.util.UUID

@Serializable
abstract class Identifiable {
    @Serializable(UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
}