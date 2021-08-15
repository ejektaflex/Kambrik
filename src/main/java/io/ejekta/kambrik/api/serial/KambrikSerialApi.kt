package io.ejekta.kambrik.api.serial

import io.ejekta.kambrik.api.serial.serializers.BlockPosSerializer
import io.ejekta.kambrik.api.serial.serializers.BlockRefSerializer
import io.ejekta.kambrik.api.serial.serializers.BoxSerializer
import io.ejekta.kambrik.api.serial.serializers.ItemRefSerializer
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class KambrikSerialApi {

    val DefaultSerializers = SerializersModule {
        // Built in data classes
        contextual(BlockPos::class, BlockPosSerializer)
        contextual(Box::class, BoxSerializer)
        // Referential serializers
        contextual(Item::class, ItemRefSerializer)
        contextual(Block::class, BlockRefSerializer)
    }

}