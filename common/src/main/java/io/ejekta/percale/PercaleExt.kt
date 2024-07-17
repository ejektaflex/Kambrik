package io.ejekta.percale

import com.google.gson.JsonParser
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import io.ejekta.percale.encoder.PassEncoder
import io.ejekta.percale.decoder.PassDecoder
import io.ejekta.percale.reverse.toSerializer
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.contextual

// ### Encoding ###

@PublishedApi
internal fun <T, U : Any> encodeWithDynamicOps(serializer: SerializationStrategy<U>, obj: U, ops: DynamicOps<T>, serialMod: SerializersModule = EmptySerializersModule()): T? {
    println("Picking based on: $serializer, ${serializer.descriptor}")
    val encoder = PassEncoder.pickEncoder(serializer.descriptor, ops, serialMod)
    println("Picked: ${encoder::class.qualifiedName}")
    encoder.encodeSerializableValue(serializer, obj)
    return encoder.getResult()
}

inline fun <T, reified U : Any> DynamicOps<T>.serialize(obj: U): T? {
    return encodeWithDynamicOps(serializer<U>(), obj, this)
}

fun <T, U : Any> DynamicOps<T>.serialize(
    obj: U,
    serializer: SerializationStrategy<U>,
    serialMod: SerializersModule = EmptySerializersModule()
): T? {
    return encodeWithDynamicOps(serializer, obj, this, serialMod)
}


// ### Decoding ###

@OptIn(ExperimentalSerializationApi::class)
fun <T, U : Any> decodeWithDynamicOps(serializer: DeserializationStrategy<U>, obj: T, ops: DynamicOps<T>, serialMod: SerializersModule = EmptySerializersModule()): U {
    val decoder = PassDecoder.pickDecoder(serializer.descriptor, ops, obj, 0, serialMod)
    println("Picked: ${decoder::class.simpleName}")
    return serializer.deserialize(decoder)
}

inline fun <T, reified U : Any> DynamicOps<in T>.deserialize(obj: T): U {
    return decodeWithDynamicOps(serializer<U>(), obj, this)
}

fun <T, U : Any> DynamicOps<T>.deserialize(obj: T, serializer: DeserializationStrategy<U>): U {
    return decodeWithDynamicOps(serializer, obj, this)
}

// ### Codec

fun <U : Any> KSerializer<U>.toCodec(serialMod: SerializersModule = EmptySerializersModule()): Codec<U> {
    return object : Codec<U> {
        override fun <T : Any> encode(input: U, ops: DynamicOps<T>, prefix: T?): DataResult<T> {
            val result = encodeWithDynamicOps(this@toCodec, input, ops, serialMod)!!
            return DataResult.success(result)
        }

        override fun <T : Any?> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<U, T>> {
            val result = decodeWithDynamicOps(this@toCodec, input, ops, serialMod)
            return DataResult.success(Pair(result, ops.empty()))
        }
    }
}

inline fun <reified A : Any> SerializersModuleBuilder.contextualCodec(codec: Codec<A>) {
    contextual(codec.toSerializer())
}
