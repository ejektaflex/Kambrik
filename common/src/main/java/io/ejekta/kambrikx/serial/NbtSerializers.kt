@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.serial

import io.ejekta.kambrik.ext.toTag
import io.ejekta.kambrik.internal.KambrikExperimental
import io.ejekta.kambrikx.internal.serial.decoders.BaseTagDecoder
import io.ejekta.kambrikx.internal.serial.encoders.BaseTagEncoder
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.NbtElement

@KambrikExperimental
@Serializer(forClass = NbtElement::class)
object TagSerializer : KSerializer<NbtElement> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TagSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: NbtElement) { encoder.encodeString(value.asString()) }
    override fun deserialize(decoder: Decoder): NbtElement = decoder.decodeString().toTag()

    @Suppress("UNCHECKED_CAST")
    operator fun <T> invoke(): KSerializer<T> {
        return TagSerializer as KSerializer<T>
    }
}

@KambrikExperimental
@OptIn(InternalSerializationApi::class)
@Serializer(forClass = NbtElement::class)
object DynTagSerializer : KSerializer<NbtElement> {
    @OptIn(InternalSerializationApi::class)
    private fun getDesc(): SerialDescriptor {
        return buildClassSerialDescriptor("DynamicTag") { }
    }
    override val descriptor: SerialDescriptor = getDesc()

    override fun serialize(encoder: Encoder, value: NbtElement) {
        if (encoder is BaseTagEncoder) {
            encoder.encodeNbtTag(value)
        } else {
            TagSerializer.serialize(encoder, value)
        }
    }
    override fun deserialize(decoder: Decoder): NbtElement {
        return if (decoder is BaseTagDecoder) {
            decoder.decodeNbtTag()
        } else {
            TagSerializer.deserialize(decoder)
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> invoke(): KSerializer<T> {
        return DynTagSerializer as KSerializer<T>
    }
}











