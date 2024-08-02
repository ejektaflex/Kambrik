package io.ejekta.kambrik.serial

import io.ejekta.kambrik.serial.serializers.*
import io.ejekta.percale.contextualCodec
import io.ejekta.percale.reverse.NbtCompoundSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
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
        contextual(Identifier::class, IdentitySer)
        contextual(Box::class, BoxSerializer)
        contextual(NbtCompound::class, NbtCompoundSerializer)
    }

    private var networkSerializers = SerializersModule {
        include(DefaultSerializers)
    }

    fun addNetworkSerializerModule(module: SerializersModule) {
        networkSerializers = SerializersModule {
            include(DefaultSerializers)
            include(networkSerializers)
            include(module)
        }
    }

    fun networkingFormat(): Json {
        return Json { serializersModule = networkSerializers }
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