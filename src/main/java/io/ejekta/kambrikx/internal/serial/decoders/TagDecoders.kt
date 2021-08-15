package io.ejekta.kambrikx.internal.serial.decoders

import io.ejekta.kambrik.internal.KambrikExperimental
import io.ejekta.kambrikx.api.serial.NbtFormatConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import net.minecraft.nbt.*

@KambrikExperimental
@ExperimentalSerializationApi
@InternalSerializationApi
internal class TagDecoder(
    config: NbtFormatConfig,
    level: Int,
    final override var root: NbtElement
) : BaseTagDecoder(config, level) {

    override fun readTag(name: String): NbtElement {
        return NbtNull.INSTANCE
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return CompositeDecoder.DECODE_DONE
    }
}

@KambrikExperimental
@ExperimentalSerializationApi
@InternalSerializationApi
internal class TagClassDecoder(
    config: NbtFormatConfig,
    level: Int,
    override var root: NbtElement
) : BaseTagDecoder(config, level) {
    private var position = 0

    private val tagCompound: NbtCompound
        get() = root as NbtCompound

    override fun readTag(name: String): NbtElement {
        return tagCompound.get(name)!!
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (position < descriptor.elementsCount) {
            val name = descriptor.getTag(position++)
            if (name in tagCompound) {
                return position - 1
            }
        }
        return CompositeDecoder.DECODE_DONE
    }
}

@KambrikExperimental
@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
internal class TagListDecoder(
    config: NbtFormatConfig,
    level: Int,
    override var root: NbtElement
) : BaseTagDecoder(config, level) {

    private val tagList: NbtList
        get() = root as NbtList

    private val size = tagList.size
    private var currentIndex = -1

    override fun readTag(name: String) = tagList[name.toInt()]!!

    override fun elementName(desc: SerialDescriptor, index: Int): String = index.toString()

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (currentIndex < size - 1) {
            currentIndex++
            return currentIndex
        }
        return CompositeDecoder.DECODE_DONE
    }

    // do nothing, maps do not have strict keys, so strict mode check is omitted
    override fun endStructure(descriptor: SerialDescriptor) { }
}

@KambrikExperimental
@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
internal class TagMapDecoder(
    config: NbtFormatConfig,
    level: Int,
    override var root: NbtElement
) : BaseTagDecoder(config, level) {

    private val tagCompound: NbtCompound
        get() = root as NbtCompound

    private val keys = tagCompound.keys.toList()
    private val size: Int = keys.size * 2
    private var position = -1

    override fun readTag(name: String): NbtElement = if (position % 2 == 0) NbtString.of(name) else tagCompound.get(name)!!

    override fun elementName(desc: SerialDescriptor, index: Int): String = keys[index / 2]

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {

        while (position < size - 1) {
            position++
            return position
        }
        return CompositeDecoder.DECODE_DONE
    }

    // As per KSX: do nothing, maps do not have strict keys, so strict mode check is omitted
    override fun endStructure(descriptor: SerialDescriptor) {  }
}

@KambrikExperimental
@InternalSerializationApi
@OptIn(ExperimentalSerializationApi::class)
internal class TaglessDecoder(
    config: NbtFormatConfig,
    level: Int,
    override var root: NbtElement
) : BaseTagDecoder(config, level) { // May need to push a tag in init {} but doesn't seem so, so far
    init { pushTag("PRIMITIVE") }
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0
}