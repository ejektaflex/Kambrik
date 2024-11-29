package io.ejekta.kambrik.serial.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

open class RegistryObjectSerializer<T>(private val reg: () -> Registry<T>, serialName: String) : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(reg().getId(value).toString())
    }

    override fun deserialize(decoder: Decoder): T {
        val id = decoder.decodeString()
        return reg()[ResourceLocation.parse(id)] ?: throw SerializationException("Could not find saved identifier!: $id")
    }

}

object ItemRefSerializer : RegistryObjectSerializer<Item>({ BuiltInRegistries.ITEM }, "ref.Item")

object BlockRefSerializer : RegistryObjectSerializer<Block>({ BuiltInRegistries.BLOCK }, "ref.Block")

object SoundEventRefSerializer : RegistryObjectSerializer<SoundEvent>({ BuiltInRegistries.SOUND_EVENT }, "ref.SoundEvent")

