package io.ejekta.kambrik.serial.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

open class RegistryObjectSerializer<T>(private val reg: () -> Registry<T>, serialName: String) : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(reg().getId(value).toString())
    }

    override fun deserialize(decoder: Decoder): T {
        val id = decoder.decodeString()
        return reg()[Identifier(id)] ?: throw SerializationException("Could not find saved identifier!: $id")
    }

}

// Commenting these two lines out causes it to compile

//@Serializer(forClass = Item::class)
object ItemRefSerializer : RegistryObjectSerializer<Item>({ Registries.ITEM }, "ref.yarn.Item")

//@Serializer(forClass = Block::class)
object BlockRefSerializer : RegistryObjectSerializer<Block>({ Registries.BLOCK }, "ref.yarn.Block")



