package io.ejekta.kambrik.serial.serializers

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = NbtCompound::class)
object SimpleNbtSerializer : KSerializer<NbtCompound> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("NbtCompound", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: NbtCompound) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): NbtCompound {
        return StringNbtReader.parse(decoder.decodeString())
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Identifier::class)
object IdentitySer : KSerializer<Identifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Identifier", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Identifier) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): Identifier {
        return Identifier(decoder.decodeString())
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = BlockPos::class)
object BlockPosSerializer : KSerializer<BlockPos> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("yarn.BlockPos") {
        element<Int>("x")
        element<Int>("y")
        element<Int>("z")
    }

    override fun serialize(encoder: Encoder, value: BlockPos) {
        encoder.apply {
            encodeInt(value.x)
            encodeInt(value.y)
            encodeInt(value.z)
        }
    }

    override fun deserialize(decoder: Decoder): BlockPos {
        return decoder.run {
            val els = (0 until 3).map {
                decodeInt()
            }
            BlockPos(els[0], els[1], els[2])
        }
    }
}

@Serializer(forClass = Box::class)
object BoxSerializer : KSerializer<Box> {
    private val delegateSerializer = DoubleArraySerializer()
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("yarn.Box", delegateSerializer.descriptor)

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

object BlockPosSerializerOptimized : KSerializer<BlockPos> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("yarn.BlockPosOptimized", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: BlockPos) = encoder.encodeLong(value.asLong())
    override fun deserialize(decoder: Decoder): BlockPos { return BlockPos.fromLong(decoder.decodeLong()) }
}

object TextSerializer : KSerializer<Text> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("yarn.Text")
    override fun serialize(encoder: Encoder, value: Text) {
        val str = Text.Serialization.toJsonString(value)
        val json = Kambrik.Serial.Format.decodeFromString(JsonObject.serializer(), str)
        encoder.encodeSerializableValue(JsonObject.serializer(), json)
    }

    override fun deserialize(decoder: Decoder): Text {
        val str = decoder.decodeSerializableValue(JsonObject.serializer()).toString()
        return Text.Serialization.fromJson(str) ?: throw Exception("Could not deserialize the given Text!")
    }
}



