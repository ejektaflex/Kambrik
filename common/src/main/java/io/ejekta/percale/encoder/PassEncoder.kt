package io.ejekta.percale.encoder

import com.mojang.serialization.DynamicOps
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
abstract class PassEncoder<T>(open val ops: DynamicOps<T>, serialMod: SerializersModule) : AbstractEncoder() {
    abstract fun getResult(): T
    abstract fun encodeFunc(func: () -> T)
    abstract fun push(result: T)

    override val serializersModule = serialMod

    override fun encodeString(value: String) {
        encodeFunc { ops.createString(value) }
    }

    override fun encodeBoolean(value: Boolean) {
        encodeFunc { ops.createBoolean(value) }
    }

    override fun encodeDouble(value: Double) {
        encodeFunc { ops.createDouble(value) }
    }

    override fun encodeFloat(value: Float) {
        encodeFunc { ops.createFloat(value) }
    }

    override fun encodeLong(value: Long) {
        encodeFunc { ops.createLong(value) }
    }

    override fun encodeInt(value: Int) {
        encodeFunc { ops.createInt(value) }
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encodeString(enumDescriptor.getElementName(index))
    }

    companion object {
        fun <V> pickEncoder(descriptor: SerialDescriptor, ops: DynamicOps<V>, serialMod: SerializersModule): PassEncoder<V> {
            return when (descriptor.kind) {
                StructureKind.CLASS, StructureKind.MAP, is PrimitiveKind, SerialKind.ENUM, PolymorphicKind.OPEN -> PassObjectEncoder(ops, serialMod)
                StructureKind.LIST -> PassListEncoder(ops, serialMod)
                else -> throw SerializationException("Unsupported descriptor type for our DynamicOps encoder: ${descriptor.kind}, ${descriptor.kind::class}")
            }//.also { println("${descriptor.kind} -> ${it::class.qualifiedName}") }
        }
    }

}