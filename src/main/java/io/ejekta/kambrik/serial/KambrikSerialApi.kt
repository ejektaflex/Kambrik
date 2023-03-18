package io.ejekta.kambrik.serial

import io.ejekta.kambrik.serial.serializers.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

@Suppress("PropertyName")
class KambrikSerialApi {

    @Serializable
    data class StringHolder(val str: String)

    val DefaultSerializers = SerializersModule {
        // Built in data classes
        contextual(BlockPos::class, BlockPosSerializer)
        contextual(Box::class, BoxSerializer)
        contextual(Identifier::class, IdentitySer)
        contextual(PacketByteBuf::class, PacketByteBuffSer)
        contextual(ItemStack::class, ItemStackSer)
        contextual(Vec3d::class, Vec3dSerializer)
        // Referential serializers
        contextual(Item::class, ItemRefSerializer)
        contextual(Block::class, BlockRefSerializer)
        // Simple NBT Compound serializer
        contextual(NbtCompound::class, SimpleNbtSerializer)
        // Simple MutableText serializer
        contextual(MutableText::class, MutableTextSerializer)
    }

    val Format = formatFor(DefaultSerializers) {
        prettyPrint = true
    }

    fun formatFor(serialModule: SerializersModule = DefaultSerializers, builder: JsonBuilder.() -> Unit = {}): Json {
        return Json {
            this.apply {
                serializersModule = serialModule
            }.builder()
        }
    }

}