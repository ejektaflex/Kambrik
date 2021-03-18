package io.ejekta.kambrik.api.network

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.Tag
import net.minecraft.util.Identifier

interface IPacketInfo<S> {
    @Transient val id: Identifier
    @Transient val format: NbtFormat
        get() = NbtFormat.Default
    @Transient val serializer: KSerializer<S>

    fun serializePacket(s: S): Tag {
        return format.encodeToTag(serializer, s)
    }

    fun deserializePacket(tag: Tag): S {
        return format.decodeFromTag(serializer, tag)
    }

    object DummySerializer : KSerializer<Any> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AnyDummy", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): Any {
            throw Exception("Cannot perform serialization with a dummy serializer!")
        }

        override fun serialize(encoder: Encoder, value: Any) {
            throw Exception("Cannot perform serialization with a dummy serializer!")
        }
    }

    companion object {
        fun <S : Any> dummy(): IPacketInfo<S> {
            return PacketInfo(
                Identifier("dummyid", "dummypath") to DummySerializer
            ) as PacketInfo<S>
        }
    }

}