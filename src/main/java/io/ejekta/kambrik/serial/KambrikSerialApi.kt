package io.ejekta.kambrik.serial

import io.ejekta.kambrik.serial.serializers.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

@Suppress("PropertyName")
class KambrikSerialApi {

    @Serializable
    data class StringHolder(val str: String)

    val DefaultSerializers = SerializersModule {
        // Built in data classes
        contextual(BlockPos::class, BlockPosSerializer)
        contextual(Box::class, BoxSerializer)
        contextual(Identifier::class, IdentitySer)
        // Referential serializers
        contextual(Item::class, ItemRefSerializer)
        contextual(Block::class, BlockRefSerializer)
        // Simple NBT Compound serializer
        contextual(NbtCompound::class, SimpleNbtSerializer)

        contextual(Text::class, TextSerializer)
        contextual(LiteralText::class, TextSerializer as KSerializer<LiteralText>)
        //contextual(Text::class, TextSerializer as KSerializer<LiteralText>)

        //contextual(LiteralText::class, TextSerializer as KSerializer<LiteralText>)
        polymorphic(Text::class, LiteralText::class, TextSerializer as KSerializer<LiteralText>)

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