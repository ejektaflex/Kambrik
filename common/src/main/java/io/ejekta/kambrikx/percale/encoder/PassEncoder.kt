package percale.encoder

import com.mojang.serialization.DynamicOps
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder

@OptIn(ExperimentalSerializationApi::class)
abstract class PassEncoder<T>(open val ops: DynamicOps<T>) : AbstractEncoder() {
    abstract fun getResult(): T
    abstract fun encodeFunc(func: () -> T)
    abstract fun push(result: T)

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
        fun <V> pickEncoder(descriptor: SerialDescriptor, ops: DynamicOps<V>): PassEncoder<V> {
            return when (descriptor.kind) {
                StructureKind.CLASS, StructureKind.MAP, is PrimitiveKind, SerialKind.ENUM -> PassObjectEncoder(ops)
                StructureKind.LIST -> PassListEncoder(ops)
                else -> throw SerializationException("Unsupported descriptor type for our DynamicOps encoder: ${descriptor.kind}, ${descriptor.kind::class}")
            }
        }
    }

}