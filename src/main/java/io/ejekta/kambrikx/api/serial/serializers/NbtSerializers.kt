@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.api.serial.serializers

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.toCompoundTag
import io.ejekta.kambrik.ext.toMap
import io.ejekta.kambrik.ext.toTag
import io.ejekta.kambrikx.ext.internal.doStructure
import io.ejekta.kambrikx.internal.serial.decoders.BaseTagDecoder
import io.ejekta.kambrikx.internal.serial.encoders.BaseTagEncoder
import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box


@Serializer(forClass = Tag::class)
object TagSerializer : KSerializer<Tag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TagSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Tag) { encoder.encodeString(value.asString()) }
    override fun deserialize(decoder: Decoder): Tag = decoder.decodeString().toTag()

    @Suppress("UNCHECKED_CAST")
    operator fun <T> invoke(): KSerializer<T> {
        return TagSerializer as KSerializer<T>
    }
}


@OptIn(InternalSerializationApi::class)
@Serializer(forClass = Tag::class)
object DynTagSerializer : KSerializer<Tag> {
    @OptIn(InternalSerializationApi::class)
    private fun getDesc(): SerialDescriptor {
        return buildClassSerialDescriptor("DynamicTag") { }
    }
    override val descriptor: SerialDescriptor = getDesc()

    override fun serialize(encoder: Encoder, value: Tag) {
        if (encoder is BaseTagEncoder) {
            encoder.encodeNbtTag(value)
        } else {
            TagSerializer.serialize(encoder, value)
        }
    }
    override fun deserialize(decoder: Decoder): Tag {
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











