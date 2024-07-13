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
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

@Suppress("PropertyName")
class KambrikSerialApi {

    val DefaultSerializers = SerializersModule {
        contextual(Box::class, BoxSerializer)
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