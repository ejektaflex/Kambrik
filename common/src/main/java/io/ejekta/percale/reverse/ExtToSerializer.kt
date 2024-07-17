package io.ejekta.percale.reverse

import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import io.ejekta.percale.encoder.PassEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.NbtOps


fun <A> Codec<A>.toSerializer(): KSerializer<A> {
    return object : KSerializer<A> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DO_NOT_USE", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: A) {
            val pass = (encoder as? PassEncoder<*>)?.ops ?: throw Exception("Cannot serialize a non-dynamicops format with this serializer!")
            when (pass) {
                is JsonOps -> {
                    val result = this@toSerializer.encodeStart(pass, value)
                    if (result.isError) {
                        throw SerializationException("Cannot auto-serialize codec, msg: ${result.error().get().message()}")
                    }
                    val resultJsonString = result
                    println("RES: ${result.orThrow}")
                    encoder.encodeSerializableValue(GsonElementSerializer, result.orThrow)
                }
                is NbtOps -> {
                    val result = this@toSerializer.encodeStart(pass, value)
                    if (result.isError) {
                        throw SerializationException("Cannot auto-serialize codec, msg: ${result.error().get().message()}")
                    }
                    encoder.encodeSerializableValue(NbtElementSerializer, result.orThrow)
                }
                else -> throw Exception("Unknown ops type!: $pass (${pass::class.simpleName})")
            }
        }
        override fun deserialize(decoder: Decoder): A {
            TODO("Not yet implemented")
        }
    }
}

