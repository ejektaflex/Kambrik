package io.ejekta.kambrik.serial.serializers

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.internal.doStructure
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
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

@Serializer(forClass = BlockPos::class)
object BlockPosSerializer : KSerializer<BlockPos> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("yarn.BlockPos") {
        element<Int>("x")
        element<Int>("y")
        element<Int>("z")
    }

    override fun serialize(encoder: Encoder, value: BlockPos) {
        encoder.doStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.x)
            encodeIntElement(descriptor, 1, value.y)
            encodeIntElement(descriptor, 2, value.z)
        }
    }

    override fun deserialize(decoder: Decoder): BlockPos {
        return decoder.doStructure(descriptor) {
            val els = (0 until 3).map {
                decodeIntElement(descriptor, decodeElementIndex(descriptor))
            }
            BlockPos(els[0], els[1], els[2])
        }
    }
}

@Serializer(forClass = Box::class)
object BoxSerializer : KSerializer<Box> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("yarn.Box") {
        element<Double>("ax")
        element<Double>("ay")
        element<Double>("az")
        element<Double>("bx")
        element<Double>("by")
        element<Double>("bz")
    }

    override fun serialize(encoder: Encoder, value: Box) {
        encoder.doStructure(descriptor) {
            value.run {
                listOf(minX, minY, minZ, maxX, maxY, maxZ).mapIndexed { index, d ->
                    encodeDoubleElement(descriptor, index, d)
                }
            }
        }
    }

    override fun deserialize(decoder: Decoder): Box {
        return decoder.doStructure(descriptor) {
            (0 until 6).map { decodeDoubleElement(descriptor, decodeElementIndex(descriptor)) }.let {
                Box(it[0], it[1], it[2], it[3], it[4], it[5])
            }
        }
    }
}

object BlockPosSerializerOptimized : KSerializer<BlockPos> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("yarn.BlockPosOptimized", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: BlockPos) = encoder.encodeLong(value.asLong())
    override fun deserialize(decoder: Decoder): BlockPos { return BlockPos.fromLong(decoder.decodeLong()) }
}

object TextSerializer : KSerializer<Text> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("yarn.Text")
    override fun serialize(encoder: Encoder, value: Text) {
        val str = Text.Serializer.toJson(value)
        val json = Kambrik.Serial.Format.decodeFromString(JsonObject.serializer(), str)
        encoder.encodeSerializableValue(JsonObject.serializer(), json)
    }

    override fun deserialize(decoder: Decoder): Text {
        val str = decoder.decodeSerializableValue(JsonObject.serializer()).toString()
        return Text.Serializer.fromJson(str) ?: throw Exception("Could not deserialize the given Text!")
    }
}



