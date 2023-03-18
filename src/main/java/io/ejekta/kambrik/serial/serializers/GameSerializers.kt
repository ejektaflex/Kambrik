package io.ejekta.kambrik.serial.serializers

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.internal.doStructure
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

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
@Serializer(forClass = MutableText::class)
object MutableTextSerializer : KSerializer<MutableText> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("yarn.MutableText", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: MutableText) {
        encoder.encodeString(Text.Serializer.toJson(value))
    }
    override fun deserialize(decoder: Decoder): MutableText {
        return Text.Serializer.fromJson(decoder.decodeString()) ?: Text.literal("ERROR DESERIALIZING MUT-TEXT")
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Identifier::class)
object IdentitySer : KSerializer<Identifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("yarn.Identifier", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Identifier) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): Identifier {
        return Identifier(decoder.decodeString())
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Identifier::class)
object PacketByteBuffSer : KSerializer<PacketByteBuf> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("yarn.PacketByteBuf", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: PacketByteBuf) {
        encoder.encodeString(packetByteBufferToString(value))
    }
    override fun deserialize(decoder: Decoder): PacketByteBuf {
        return packetByteBufferFromString(decoder.decodeString())
    }

    fun packetByteBufferToString(packetByteBuf: PacketByteBuf): String {
        return String(packetByteBuf.writtenBytes)
    }

    fun packetByteBufferFromString(str: String): PacketByteBuf {
        val ba = str.toByteArray()
        val bb = Unpooled.buffer().writeBytes(ba)
        return PacketByteBufs.duplicate(bb)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Identifier::class)
object ItemStackSer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("yarn.ItemStack", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeString(PacketByteBuffSer.packetByteBufferToString(
            PacketByteBufs.create().apply {
                writeItemStack(value)
            }
        ))
    }
    override fun deserialize(decoder: Decoder): ItemStack {
        return PacketByteBuffSer.packetByteBufferFromString(
            decoder.decodeString()
        ).run {
            readItemStack()
        }
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


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Vec3d::class)
object Vec3dSerializer : KSerializer<Vec3d> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("yarn.Vec3d") {
        element<Double>("x")
        element<Double>("y")
        element<Double>("z")
    }

    override fun serialize(encoder: Encoder, value: Vec3d) {
        encoder.doStructure(descriptor) {
            encodeDoubleElement(descriptor, 0, value.x)
            encodeDoubleElement(descriptor, 1, value.y)
            encodeDoubleElement(descriptor, 2, value.z)
        }
    }

    override fun deserialize(decoder: Decoder): Vec3d {
        return decoder.doStructure(descriptor) {
            val els = (0 until 3).map {
                decodeDoubleElement(descriptor, decodeElementIndex(descriptor))
            }
            Vec3d(els[0], els[1], els[2])
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
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



