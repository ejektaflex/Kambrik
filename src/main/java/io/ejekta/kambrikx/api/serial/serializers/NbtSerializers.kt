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

@Serializer(forClass = Tag::class)
class TagSerializerTwo : KSerializer<Tag> {
    @OptIn(InternalSerializationApi::class)
    private fun getDesc(): SerialDescriptor {
        return buildSerialDescriptor("Doooot", PolymorphicKind.SEALED) {
            // element("JsonPrimitive", defer { JsonPrimitiveSerializer.descriptor })
            element("ByteTag", ByteTagSerializer.descriptor)
            element("StringTag", StringTagSerializer.descriptor)
        }
    }
    override val descriptor: SerialDescriptor = getDesc()
    override fun serialize(encoder: Encoder, value: Tag) {
        encoder.encodeString(value.asString())
    }
    override fun deserialize(decoder: Decoder): Tag {
        return decoder.decodeString().toTag()
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> invoke(): KSerializer<T> {
        return TagSerializer as KSerializer<T>
    }
}

@Serializer(forClass = ByteTag::class)
object ByteTagSerializer : KSerializer<ByteTag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ByteTagSerializer", PrimitiveKind.BYTE)
    override fun serialize(encoder: Encoder, value: ByteTag) {
        encoder.encodeByte(value.byte)
    }
    override fun deserialize(decoder: Decoder): ByteTag {
        return ByteTag.of(decoder.decodeByte())
    }
}

@Serializer(forClass = StringTag::class)
object StringTagSerializer : KSerializer<StringTag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StringTagSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: StringTag) {
        encoder.encodeString(value.asString())
    }
    override fun deserialize(decoder: Decoder): StringTag {
        return StringTag.of(decoder.decodeString())
    }
}

@Serializable
data class Holder(val tag: @Contextual CompoundTag)

@OptIn(InternalSerializationApi::class)
@Serializer(forClass = CompoundTag::class)
class CompoundTagSerializer : KSerializer<CompoundTag> {
    @OptIn(InternalSerializationApi::class)
    private fun getDesc(): SerialDescriptor {
        return buildClassSerialDescriptor("CompoundyTag") {
            mapSerialDescriptor(PrimitiveSerialDescriptor(
                "t", PrimitiveKind.STRING
            ), TagSerializer.descriptor)
        }
    }
    override val descriptor: SerialDescriptor = getDesc()

    override fun serialize(encoder: Encoder, value: CompoundTag) {
        if (encoder is BaseTagEncoder) {
            Kambrik.Logger.warn("Encoder is base and encoding direct tag")
            encoder.encodeNbtTag(value)
        } else {
            MapSerializer(String.serializer(), PolymorphicSerializer(Tag::class)).serialize(encoder, value.toMap())
        }
    }
    override fun deserialize(decoder: Decoder): CompoundTag {
        if (decoder is BaseTagDecoder) {
            Kambrik.Logger.warn("Decoder is base and encoding direct tag")
            return decoder.decodeNbtTag() as CompoundTag
        } else {
            val mapped = MapSerializer(String.serializer(), PolymorphicSerializer(Tag::class)).deserialize(decoder)
            return mapped.toCompoundTag()
        }
    }
}

@Serializer(forClass = Tag::class)
class TagFinalSer : KSerializer<Tag> {
    @OptIn(InternalSerializationApi::class)
    private fun getDesc(): SerialDescriptor {
        return buildSerialDescriptor("kotlinx.serialization.Polymorphic", PolymorphicKind.OPEN) {
            element("type", String.serializer().descriptor)
            element(
                "value",
                buildSerialDescriptor("kotlinx.serialization.Polymorphic<${Tag::class.simpleName}>", SerialKind.CONTEXTUAL)
            )
        }
    }
    override val descriptor: SerialDescriptor = getDesc()
    override fun serialize(encoder: Encoder, value: Tag) {
        PolymorphicSerializer(Tag::class).serialize(encoder, value)
    }
    override fun deserialize(decoder: Decoder): Tag {
        return PolymorphicSerializer(Tag::class).deserialize(decoder)
    }
}

@Serializer(forClass = Tag::class)
class TagFinalSerTwo : KSerializer<Tag> {
    @OptIn(InternalSerializationApi::class)
    private fun getDesc(): SerialDescriptor {
        return buildSerialDescriptor("kotlinx.serialization.Polymorphic", PolymorphicKind.OPEN) {
            element("type", String.serializer().descriptor)
            element(
                "value",
                buildSerialDescriptor("kotlinx.serialization.Polymorphic<${Tag::class.simpleName}>", SerialKind.CONTEXTUAL)
            )
        }
    }
    override val descriptor: SerialDescriptor = getDesc()
    override fun serialize(encoder: Encoder, value: Tag) {
        PolymorphicSerializer(Tag::class).serialize(encoder, value)
    }
    override fun deserialize(decoder: Decoder): Tag {
        return PolymorphicSerializer(Tag::class).deserialize(decoder)
    }
}











