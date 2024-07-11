package io.ejekta.kambrikx.percale

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import kotlinx.serialization.*
import percale.decoder.PassDecoder
import percale.encoder.PassEncoder

@OptIn(ExperimentalSerializationApi::class)
fun <T, U : Any> encodeWithDynamicOps(serializer: SerializationStrategy<U>, obj: U, ops: DynamicOps<T>): T? {
    val encoder = PassEncoder.pickEncoder(serializer.descriptor, ops)
    encoder.encodeSerializableValue(serializer, obj)
    return encoder.getResult()
}

inline fun <T, reified U : Any> DynamicOps<T>.serialize(obj: U): T? {
    return encodeWithDynamicOps(serializer<U>(), obj, this)
}

fun <T, U : Any> DynamicOps<T>.serialize(obj: U, serializer: SerializationStrategy<U>): T? {
    return encodeWithDynamicOps(serializer, obj, this)
}

fun <U : Any> SerializationStrategy<U>.toEncoder(): Encoder<U> {
    return object : Encoder<U> {
        override fun <T : Any> encode(input: U, ops: DynamicOps<T>, prefix: T): DataResult<T> {
            val result = encodeWithDynamicOps(this@toEncoder, input, ops)!!
            return DataResult.success(result)
        }
    }
}

// ### Decoding ###

@OptIn(ExperimentalSerializationApi::class)
fun <T, U : Any> decodeWithDynamicOps(serializer: DeserializationStrategy<U>, obj: T, ops: DynamicOps<T>): U {
    val decoder = PassDecoder.pickDecoder(serializer.descriptor, ops, obj)
    return serializer.deserialize(decoder)
}

inline fun <T, reified U : Any> DynamicOps<in T>.deserialize(obj: T): U {
    return decodeWithDynamicOps(serializer<U>(), obj, this)
}

fun <T, U : Any> DynamicOps<T>.deserialize(obj: T, serializer: DeserializationStrategy<U>): U {
    return decodeWithDynamicOps(serializer, obj, this)
}

fun <U : Any> DeserializationStrategy<U>.toDecoder(): Decoder<U> {
    return object : Decoder<U> {
        override fun <T : Any?> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<U, T>> {
            val result = decodeWithDynamicOps(this@toDecoder, input, ops)
            return DataResult.success(Pair(result, ops.empty()))
        }
    }
}

// ### Codec

fun <U : Any> KSerializer<U>.toCodec(): Codec<U> {
    return object : Codec<U> {
        override fun <T : Any> encode(input: U, ops: DynamicOps<T>, prefix: T): DataResult<T> {
            val result = encodeWithDynamicOps(this@toCodec, input, ops)!!
            return DataResult.success(result)
        }

        override fun <T : Any?> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<U, T>> {
            val result = decodeWithDynamicOps(this@toCodec, input, ops)
            return DataResult.success(Pair(result, ops.empty()))
        }
    }
}