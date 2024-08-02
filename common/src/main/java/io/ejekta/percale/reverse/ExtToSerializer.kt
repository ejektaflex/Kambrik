package io.ejekta.percale.reverse

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.EitherCodec
import com.mojang.serialization.codecs.ListCodec
import io.ejekta.percale.decoder.PassDecoder
import io.ejekta.percale.encoder.PassEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.RegistryOps
import net.minecraft.util.Uuids
import net.minecraft.util.math.BlockPos

fun <A> Codec<A>.toSerializer(like: KSerializer<*>): KSerializer<A> {
    return toSerializer(like.descriptor)
}

private val baseLookup: Map<Codec<*>, KSerializer<*>> = mapOf(
    Codec.BOOL to Boolean.serializer(),
    Codec.BYTE to Byte.serializer(),
    // Codec.BYTE_BUFFER
    Codec.DOUBLE to Double.serializer(),
    // Codec.EMPTY
    Codec.FLOAT to Float.serializer(),
    Codec.INT to Int.serializer(),
    Codec.INT_STREAM to IntArraySerializer(),
    Codec.LONG to Long.serializer(),
    Codec.LONG_STREAM to LongArraySerializer(),
    // Codec.PASSTHROUGH
    Codec.SHORT to Short.serializer(),
    Codec.STRING to String.serializer()
)

private val mcLookup: Map<Codec<*>, KSerializer<*>> = mapOf(

)

fun <A> Codec<A>.guessSerializer(): KSerializer<*>? {
    return getSerializerFromName(toString())
}

private fun getSerializerFromName(codecName: String): KSerializer<*>? {
    val prefix = codecName.substringBefore("[")
    println("Prefix: $prefix")
    val heuristic: KSerializer<*>? = when (prefix) {
        "IntStream" -> baseLookup[Codec.INT_STREAM]
        "String" -> baseLookup[Codec.STRING]
        "ListCodec" -> {
            val listCodecPath = codecName.split("][").first() + "]"
            println("LC: '$listCodecPath'")
            val listCodecType = codecName.substringAfter("[").substringBeforeLast("]")
            println("LCT: $listCodecType")

            val subCodecSerial = getSerializerFromName(listCodecType)
            //val subCodec = codecLookup[listCodecType]

            println("SCS: $subCodecSerial")
            //println("SC: $subCodec")

            subCodecSerial?.let {
                ListSerializer(it)
            }
        }
        else -> null
    }
    return heuristic
}

// TODO
//fun <A> MapCodec<A>.toSerializer(typeDescriptor: SerialDescriptor? = null): KSerializer<Map<String, A>> {
//    return MapSerializer(String.serializer(), codec().toSerializer(typeDescriptor))
//}

fun <A> Codec<A>.toSerializer(typeDescriptor: SerialDescriptor? = null): KSerializer<A> {
    return object : KSerializer<A> {
        override val descriptor: SerialDescriptor
            get() {
                // If we supply one directly, use it
                if (typeDescriptor != null) {
                    return typeDescriptor
                }

                // Otherwise, if an NbtCompound codec, then use that
                if (this@toSerializer == NbtCompound.CODEC) {
                    return NbtCompoundSerializer.descriptor
                }

                // Otherwise, static lookup
//                descriptorLookup[this@toSerializer]?.let {
//                    return it
//                }
                // Otherwise, static list codec type lookup
                println(this@toSerializer is ListCodec<*>)

                println("Must heuristically generate a SerialDescriptor for: ${this@toSerializer}")

                // Otherwise, icky heuristic lookup
                val heuristic = guessSerializer()?.descriptor

                return heuristic ?: PrimitiveSerialDescriptor("UNKNOWN_SER", PrimitiveKind.STRING)
            }
        override fun serialize(encoder: Encoder, value: A) {
            val pass = (encoder as? PassEncoder<*>)?.ops ?: throw Exception("Cannot serialize a non-dynamicops format with this serializer!")
            when (pass) {
                is JsonOps, is RegistryOps -> {
                    val result = this@toSerializer.encodeStart(pass, value)
                    if (result.isError) {
                        throw SerializationException("Cannot auto-serialize codec, msg: ${result.error().get().message()}")
                    }
                    println("RES: ${result.orThrow}")
                    encoder.encodeSerializableValue(GsonElementSerializer, result.orThrow as JsonElement)
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
            val passDecoder = (decoder as? PassDecoder<*>) ?: throw Exception("Cannot serialize a non-dynamicops format with this serializer!")
            val pass = passDecoder.ops as DynamicOps<Any>
            when (pass) {
                is JsonOps, is RegistryOps -> {
                    //val result = this@toSerializer.parse(pass, passDecoder.input)
                    println("Must deser: ${passDecoder.input}, ${passDecoder.input!!::class.simpleName}")
                    val jsony = passDecoder.decodeSerializableValue(GsonElementSerializer)
                    println("JSONY: $jsony")
                    val result = this@toSerializer.parse(pass, jsony)
                    println("RESULT: $result")
                    return result.orThrow
                }
                is NbtOps -> {
                    val nbty = passDecoder.decodeSerializableValue(NbtElementSerializer)
                    val result = this@toSerializer.parse(pass, nbty)
                    return result.orThrow
                }
            }


            TODO("Not yet implemented")
        }
    }
}

