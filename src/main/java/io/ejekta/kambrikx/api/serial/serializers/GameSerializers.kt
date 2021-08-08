package io.ejekta.kambrikx.api.serial.serializers

import io.ejekta.kambrik.ext.toMap
import io.ejekta.kambrikx.ext.internal.doCollection
import io.ejekta.kambrikx.ext.internal.doStructure
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box




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
@Serializer(forClass = ItemStack::class)
object ItemStackSer : KSerializer<ItemStack> {
    private fun getDesc(): SerialDescriptor {
        return PrimitiveSerialDescriptor("ItemStack", PrimitiveKind.STRING)
    }
    override val descriptor: SerialDescriptor = getDesc()
    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeSerializableValue(TagSerializer, value.writeNbt(NbtCompound()))
    }
    override fun deserialize(decoder: Decoder): ItemStack {
        val tag = decoder.decodeSerializableValue(TagSerializer) as NbtCompound
        return ItemStack.fromNbt(tag)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ItemStack::class)
class ItemStackSerTwo : KSerializer<ItemStack> {
    private fun getDesc(): SerialDescriptor {
        return serialDescriptor<Map<String, NbtElement>>()
    }
    override val descriptor: SerialDescriptor = getDesc()
    override fun serialize(encoder: Encoder, value: ItemStack) {
        //encoder.encodeSerializableValue(TagSerializer, value.toTag(NbtCompound()) as NbtElement)
        val tagged = value.writeNbt(NbtCompound())
        val mapped = tagged.toMap()
        encoder.encodeSerializableValue(
            MapSerializer(String.serializer(), TagSerializer),
            mapped
        )
    }
    override fun deserialize(decoder: Decoder): ItemStack {
        val tag = decoder.decodeSerializableValue(TagSerializer) as NbtCompound
        return ItemStack.fromNbt(tag)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ItemStack::class)
class ItemStackSerThree : KSerializer<ItemStack> {
    private fun getDesc(): SerialDescriptor {
        return serialDescriptor<Map<String, NbtElement>>()
    }
    override val descriptor: SerialDescriptor = getDesc()
    override fun serialize(encoder: Encoder, value: ItemStack) {
        //encoder.encodeSerializableValue(TagSerializer, value.toTag(NbtCompound()) as NbtElement)
        val mapped = value.writeNbt(NbtCompound()).toMap()
        MapSerializer(String.serializer(), TagSerializer).serialize(encoder, mapped)
    }
    override fun deserialize(decoder: Decoder): ItemStack {
        val tag = decoder.decodeSerializableValue(TagSerializer) as NbtCompound
        return ItemStack.fromNbt(tag)
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
            val x = decodeIntElement(descriptor, 0)
            val y = decodeIntElement(descriptor, 1)
            val z = decodeIntElement(descriptor, 2)
            BlockPos(x, y, z)
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
            (0 until 6).map { decodeDoubleElement(descriptor, it) }.let {
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



