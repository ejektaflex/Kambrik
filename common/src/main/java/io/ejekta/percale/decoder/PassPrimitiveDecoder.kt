package io.ejekta.percale.decoder

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class PassPrimitiveDecoder<T>(override val ops: DynamicOps<T>, override val input: T, level: Int, serialMod: SerializersModule) : PassDecoder<T>(ops, level, serialMod) {

    override val currentValue: T?
        get() = input

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        throw SerializationException("Cannot do a structure on a primitive!")
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        throw SerializationException("Cannot decode index on a primitive!")
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex(decodeString())
    }

    override fun <V> decodeFunc(func: () -> DataResult<V>): V {
        val dataResult = func()
        return dataResult.orThrow
    }
}