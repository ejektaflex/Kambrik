package io.ejekta.kambrik.serial.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box

object SimpleNbtSerializer : KSerializer<NbtCompound> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kambrik.NbtCompound", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: NbtCompound) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): NbtCompound {
        return StringNbtReader.parse(decoder.decodeString())
    }
}

object IdentitySer : KSerializer<Identifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kambrik.Identifier", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Identifier) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): Identifier {
        return Identifier.of(decoder.decodeString())
    }
}

object BoxSerializer : KSerializer<Box> {
    private val delegateSerializer = DoubleArraySerializer()
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("kambrik.Box", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Box) {
        encoder.apply {
            value.run {
                encoder.encodeSerializableValue(delegateSerializer, doubleArrayOf(minX, minY, minZ, maxX, maxY, maxZ))
            }
        }
    }

    override fun deserialize(decoder: Decoder): Box {
        val arr = decoder.decodeSerializableValue(delegateSerializer)
        return arr.run {
            Box(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5])
        }
    }
}

