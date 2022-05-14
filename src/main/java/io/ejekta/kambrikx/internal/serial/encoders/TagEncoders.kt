package io.ejekta.kambrikx.internal.serial.encoders

import io.ejekta.kambrik.internal.KambrikExperimental
import io.ejekta.kambrikx.serial.NbtFormatConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encoding.AbstractEncoder
import net.minecraft.nbt.*

@KambrikExperimental
@InternalSerializationApi
internal class TagEncoder(config: NbtFormatConfig) : BaseTagEncoder(config) {
    override var root: NbtElement = NbtEnd.INSTANCE

    // Currently, primitive encodings directly call addTag("PRIMITIVE", tag)
    override fun addTag(name: String?, tag: NbtElement) {
        root = tag
    }

    override val propogate: NbtElement.() -> Unit = { root = this }
}

@KambrikExperimental
@InternalSerializationApi
@ExperimentalSerializationApi
internal class TagClassEncoder(config: NbtFormatConfig, level: Int, onEnd: (NbtElement) -> Unit = {}) : BaseTagEncoder(config, level, onEnd) {
    override var root = NbtCompound()
    override fun addTag(name: String?, tag: NbtElement) {
        root.put(name, tag)
    }
}

@KambrikExperimental
@InternalSerializationApi
internal class TagListEncoder(config: NbtFormatConfig, level: Int, onEnd: (NbtElement) -> Unit) : BaseTagEncoder(config, level, onEnd) {
    override val root = NbtList()
    override fun addTag(name: String?, tag: NbtElement) {
        if (name != config.classDiscriminator) {
            root.add(name!!.toInt(), tag)
        }
    }
}

@KambrikExperimental
@InternalSerializationApi
@ExperimentalSerializationApi
internal class TagMapEncoder(config: NbtFormatConfig, level: Int, onEnd: (NbtElement) -> Unit = {}) : BaseTagEncoder(config, level, onEnd) {
    override var root = NbtCompound()
    private var theKey = ""
    private var isKey = true

    override fun addTag(name: String?, tag: NbtElement) {
        //println("Boop $name '$theKey' $tag (${tag::class})")
        if (name != config.classDiscriminator) {
            if (isKey) {
                theKey = tag.asString() ?: "DEFAULT_KEY"
            } else {
                root.put(theKey, tag)
            }
            isKey = !isKey
        }
    }
}

@KambrikExperimental
@InternalSerializationApi
@ExperimentalSerializationApi
internal class TaglessEncoder(config: NbtFormatConfig) : AbstractEncoder() {
    override val serializersModule = config.serializersModule
    lateinit var root: NbtElement
    override fun encodeInt(value: Int) { root = NbtInt.of(value) }
    override fun encodeString(value: String) { root = NbtString.of(value) }
    override fun encodeBoolean(value: Boolean) { root = NbtByte.of(value) }
    override fun encodeDouble(value: Double) { root = NbtDouble.of(value) }
    override fun encodeByte(value: Byte) { root = NbtByte.of(value) }
    override fun encodeChar(value: Char) { root = NbtString.of(value.toString()) }
    override fun encodeFloat(value: Float) { root = NbtFloat.of(value) }
    override fun encodeLong(value: Long) { root = NbtLong.of(value) }
    override fun encodeShort(value: Short) { root = NbtShort.of(value) }
}