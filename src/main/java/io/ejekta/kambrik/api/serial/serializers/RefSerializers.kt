package io.ejekta.kambrik.api.serial.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@OptIn(ExperimentalSerializationApi::class)
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
object ItemRefSerializer : RegistryObjectSerializer<Item>({ Registry.ITEM }, "ref.yarn.Item")

//@Serializer(forClass = Block::class)
object BlockRefSerializer : RegistryObjectSerializer<Block>({ Registry.BLOCK }, "ref.yarn.Block")



