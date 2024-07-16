package io.ejekta.percale.decoder

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class PassListDecoder<T>(override val ops: DynamicOps<T>, override val input: T, level: Int, serialMod: SerializersModule) : PassDecoder<T>(ops, level, serialMod) {

    private var inputList = ops.getStream(input).result().get().toList()
    private var currentIndex = -1


    override val currentValue: T?
        get() {
            return inputList[currentIndex]
        }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (currentIndex < 0) {
            return this
        }
        val pickedDecoder = pickDecoder(descriptor, ops, currentValue!!, level, serializersModule)

        return pickedDecoder
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        currentIndex += 1
        return if (currentIndex < inputList.size) currentIndex else CompositeDecoder.DECODE_DONE
    }

    override fun <V> decodeFunc(func: () -> DataResult<V>): V {
        val dataResult = func()
        return dataResult.orThrow
    }

}