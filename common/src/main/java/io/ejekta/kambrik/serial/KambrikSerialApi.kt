package io.ejekta.kambrik.serial

import io.ejekta.kambrik.serial.serializers.BoxSerializer
import io.ejekta.kambrik.serial.serializers.IdentitySer
import io.ejekta.kambrik.serial.serializers.Vec3DSer
import io.ejekta.percale.contextualCodec
import io.ejekta.percale.reverse.CompoundTagSerializer
import io.ejekta.percale.reverse.toSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

@Suppress("PropertyName")
class KambrikSerialApi {

    val DefaultSerializers = SerializersModule {
        contextual(Identifier::class, IdentitySer)
        contextual(AABB::class, BoxSerializer)
        contextual(Vec3::class, Vec3DSer)
        contextual(CompoundTag::class, CompoundTagSerializer)
        //contextualCodec(ItemStack.CODEC)
        contextual(ItemStack::class, ItemStack.CODEC.toSerializer(JsonObject.serializer()))
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