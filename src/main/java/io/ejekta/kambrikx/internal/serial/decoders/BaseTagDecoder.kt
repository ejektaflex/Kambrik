package io.ejekta.kambrikx.internal.serial.decoders

import io.ejekta.kambrik.internal.KambrikExperimental
import io.ejekta.kambrikx.serial.NbtFormatConfig
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.*

@KambrikExperimental
@InternalSerializationApi
internal abstract class BaseTagDecoder(
    @JvmField protected val config: NbtFormatConfig,
    var level: Int = 0
) : NamedValueDecoder() {

    abstract val root: NbtElement

    private fun currentTag(): NbtElement = currentTagOrNull?.let { readTag(it) } ?: root

    open fun readTag(name: String): NbtElement {
        return root
    }

    @ExperimentalSerializationApi
    override val serializersModule: SerializersModule = config.serializersModule

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        config.logInfo(level, "Parse: ${descriptor.kind} with tag ${currentTag()} (am: ${this::class.simpleName})")
        return when (descriptor.kind) {
            is PolymorphicKind -> TagMapDecoder(config, level + 1, currentTag())
            StructureKind.CLASS -> TagClassDecoder(config, level + 1, currentTag())
            StructureKind.LIST -> TagListDecoder(config, level + 1, currentTag())
            StructureKind.MAP -> TagMapDecoder(config, level + 1, currentTag())
            else -> throw Exception("Cannot decode a ${descriptor.kind} yet with beginStructure!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalSerializationApi
    private fun <T : Any> getPolymorphicDeserializer(ser: DeserializationStrategy<T>): DeserializationStrategy<out T> {
        val abs = ser as AbstractPolymorphicSerializer<T>
        val typed = (currentTag() as? NbtCompound ?: return ser).getString(config.classDiscriminator)
        if (typed == "") {
            throw SerializationException("Tried to get the polymorphic serializer in ${currentTag()} but failed!")
        }
        return abs.findPolymorphicSerializer(this, typed)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun <T : Any> getContextualDeserialized(ser: ContextualSerializer<T>): T {
        return ser.deserialize(this)
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalSerializationApi
    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {

        if (deserializer !is AbstractPolymorphicSerializer<*>) {
            return deserializer.deserialize(this)
        }

        val actualSerializer = getPolymorphicDeserializer(deserializer as DeserializationStrategy<Any>) as DeserializationStrategy<T>

        return TagMapDecoder(config, level, currentTag()).decodeSerializableValue(actualSerializer)
    }

    fun decodeNbtTag(): NbtElement = readTag(popTag())
    override fun decodeTaggedInt(tag: String): Int = (readTag(tag) as NbtInt).intValue()
    override fun decodeTaggedString(tag: String): String = (readTag(tag) as NbtString).asString()
    override fun decodeTaggedBoolean(tag: String): Boolean = (readTag(tag) as NbtByte).byteValue() > 0
    override fun decodeTaggedDouble(tag: String): Double = (readTag(tag) as NbtDouble).doubleValue()
    override fun decodeTaggedByte(tag: String): Byte = (readTag(tag) as NbtByte).byteValue()
    override fun decodeTaggedChar(tag: String): Char = (readTag(tag) as NbtString).asString().first()
    override fun decodeTaggedFloat(tag: String): Float = (readTag(tag) as NbtFloat).floatValue()
    override fun decodeTaggedLong(tag: String): Long = (readTag(tag) as NbtLong).longValue()
    override fun decodeTaggedShort(tag: String): Short = (readTag(tag) as NbtShort).shortValue()

    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeTaggedEnum(tag: String, enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex((readTag(tag) as NbtString).asString())
    }

    override fun decodeTaggedNull(tag: String): Nothing? = null

    override fun decodeTaggedNotNullMark(tag: String): Boolean {
        val read = readTag(tag)
        return read != config.nullTag
    }
}