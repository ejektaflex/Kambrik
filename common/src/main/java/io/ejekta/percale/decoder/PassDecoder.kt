package io.ejekta.percale.decoder

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalSerializationApi::class)
abstract class PassDecoder<T>(open val ops: DynamicOps<T>, val level: Int, serialMod: SerializersModule) : AbstractDecoder() {
    abstract fun <V> decodeFunc(func: () -> DataResult<V>): V
    abstract val currentValue: T?
    abstract val input: T

//    fun debug(item: Any) {
//        println("${" ".repeat(level * 2)}* [${hashCode().toString().drop(3)}] * $item")
//    }

    override val serializersModule = serialMod

    override fun decodeString(): String {
        return decodeFunc { ops.getStringValue(currentValue) }
    }

    override fun decodeInt(): Int {
        return decodeFunc { ops.getNumberValue(currentValue) }.toInt()
    }

    override fun decodeBoolean(): Boolean {
        return decodeFunc { ops.getBooleanValue(currentValue) }
    }

    override fun decodeLong(): Long {
        return decodeFunc { ops.getNumberValue(currentValue) }.toLong()
    }

    override fun decodeFloat(): Float {
        return decodeFunc { ops.getNumberValue(currentValue) }.toFloat()
    }

    override fun decodeDouble(): Double {
        return decodeFunc { ops.getNumberValue(currentValue) }.toDouble()
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex(decodeString())
    }

    override fun decodeNotNullMark(): Boolean {
        return true
    }

    override fun decodeNull(): Nothing? {
        return null
    }

    override fun decodeSequentially(): Boolean {
        return false
    }

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        return deserializer.deserialize(pickDecoder(deserializer.descriptor, ops, currentValue ?: input, level + 1, serializersModule))
    }

    companion object {
        fun <V> pickDecoder(descriptor: SerialDescriptor, ops: DynamicOps<V>, input: V, level: Int = 0, serialMod: SerializersModule): PassDecoder<V> {
            return when (descriptor.kind) {
                StructureKind.CLASS, PolymorphicKind.OPEN -> PassObjectDecoder(ops, input, level + 1, serialMod)
                is PrimitiveKind, SerialKind.ENUM, SerialKind.CONTEXTUAL  -> PassPrimitiveDecoder(ops, input, level + 1, serialMod)
                StructureKind.MAP -> PassMapDecoder(ops, input, level + 1, serialMod)
                StructureKind.LIST -> PassListDecoder(ops, input, level + 1, serialMod)
                else -> throw SerializationException("Unsupported descriptor type for our DynamicOps encoder: ${descriptor.kind}, ${descriptor.kind::class}")
            }
        }
    }
}