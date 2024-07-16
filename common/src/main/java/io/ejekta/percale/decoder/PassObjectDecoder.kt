package io.ejekta.percale.decoder

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import io.ejekta.percale.reverse.NbtIntSerializer
import io.ejekta.percale.reverse.NbtStringSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtString
import kotlin.jvm.optionals.getOrNull

@OptIn(ExperimentalSerializationApi::class)
class PassObjectDecoder<T>(override val ops: DynamicOps<T>, override val input: T, level: Int, serialMod: SerializersModule) : PassDecoder<T>(ops, level, serialMod) {

    private var inputMap =
        ops.getMap(input).result().getOrNull()
    private var inputKeys = mutableListOf<String>()
    private var currentIndex = -1

    private val currentKey: String?
        get() {
            // If no input keys, is not a map and is just primitive input
            return if (inputKeys.isEmpty()) {
                null
            } else {
                inputKeys[currentIndex]
            }
        }

    override val currentValue: T?
        get() {
            return inputMap?.get(currentKey)
        }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        inputKeys = (0..<descriptor.elementsCount).map { descriptor.getElementName(it) }.toMutableList()
        return this // maybe only if currentIndex < 0 ?
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        currentIndex += 1
        return if (currentIndex < descriptor.elementsCount) currentIndex else CompositeDecoder.DECODE_DONE
    }

    override fun <V> decodeFunc(func: () -> DataResult<V>): V {
        val dataResult = func()
        return dataResult.orThrow
    }

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T {
        println("El deser: $descriptor, $deserializer, $index, $previousValue")
        return super.decodeSerializableElement(descriptor, index, deserializer, previousValue)
    }

    override fun <A> decodeSerializableValue(deserializer: DeserializationStrategy<A>): A {
        println("Obj deser: $deserializer")
        val serial = deserializer.descriptor
        val abc = deserializer.descriptor.kind
        println(deserializer)

        val pickedSer = when (input) {
            is NbtString -> NbtStringSerializer
            is NbtInt -> NbtIntSerializer
            else -> null
        }

        pickedSer?.let {
            println("Doing nbt decode deser..")
            val decoder = pickDecoder(it.descriptor, ops, input, level + 1, serializersModule)
            println("Time to start! Using: ${decoder::class.simpleName}")
            return decoder.decodeSerializableValue(it) as A
        }
        println("No NBT picking, defaulting..")
        return super.decodeSerializableValue(deserializer)
    }
}