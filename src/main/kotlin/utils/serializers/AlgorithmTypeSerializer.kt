package utils.serializers

import common.AlgorithmType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object AlgorithmTypeSerializer: KSerializer<AlgorithmType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("AlgorithmType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AlgorithmType =
        enumValueOf(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: AlgorithmType) {
        encoder.encodeString(value.toString())
    }
}