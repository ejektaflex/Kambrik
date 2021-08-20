package io.ejekta.kambrik.api.serial

import io.ejekta.kambrik.api.serial.serializers.*
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

@Suppress("PropertyName")
class KambrikSerialApi {

    val DefaultSerializers = SerializersModule {
        // Built in data classes
        contextual(BlockPos::class, BlockPosSerializer)
        contextual(Box::class, BoxSerializer)
        // Referential serializers
        contextual(Item::class, ItemRefSerializer)
        contextual(Block::class, BlockRefSerializer)
        // Simple NBT Compound serializer
        contextual(NbtCompound::class, SimpleNbtSerializer)
    }

}