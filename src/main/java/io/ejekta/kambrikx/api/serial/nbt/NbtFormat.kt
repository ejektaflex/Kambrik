package io.ejekta.kambrikx.api.serial.nbt

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.KambrikMod
import io.ejekta.kambrikx.api.serial.serializers.*
import io.ejekta.kambrikx.internal.serial.decoders.TagDecoder
import io.ejekta.kambrikx.internal.serial.decoders.TaglessDecoder
import io.ejekta.kambrikx.internal.serial.encoders.TagEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TaglessEncoder
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

@OptIn(InternalSerializationApi::class)
class NbtFormatConfig {

    private val nbtEncodingMarker = Kambrik.Logging.createMarker(KambrikMod.idOf("nbt"))

    private val logger = Kambrik.Logger

    var showDebug = false

    internal fun logInfo(level: Int, msg: String) {
        if (showDebug) {
            logger.info(nbtEncodingMarker, "\t".repeat(level) + msg)
        }
    }

    var classDiscriminator: String = "type"

    @ExperimentalSerializationApi
    var serializersModule: SerializersModule = SerializersModule {
        include(NbtFormat.BuiltInSerializers)
        include(NbtFormat.ReferenceSerializers)
    }

    var writePolymorphic = true

    var nullTag: Tag = StringTag.of("_serial_null")
}

@OptIn(InternalSerializationApi::class)
open class NbtFormat internal constructor(val config: NbtFormatConfig) : SerialFormat {

    constructor(configFunc: NbtFormatConfig.() -> Unit) : this(NbtFormatConfig().apply(configFunc))

    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule = EmptySerializersModule + config.serializersModule

    companion object {

        @Suppress("UNCHECKED_CAST")
        val BuiltInSerializers = SerializersModule {

            // Polymorphic
            /*
            polymorphic(Tag::class) {
                subclass(ByteTag::class, ByteTagSerializer)
                subclass(StringTag::class, StringTagSerializer)
            }

             */

            // Contextual

            // Tags
            contextual(Tag::class, DynTagSerializer)
            contextual(CompoundTag::class, DynTagSerializer())
            contextual(IntTag::class, DynTagSerializer())
            contextual(StringTag::class, DynTagSerializer())
            contextual(DoubleTag::class, DynTagSerializer())
            contextual(ByteTag::class, DynTagSerializer())
            contextual(FloatTag::class, DynTagSerializer())
            contextual(LongTag::class, DynTagSerializer())
            contextual(ShortTag::class, DynTagSerializer())

            // Complex Tags
            contextual(LongArrayTag::class, DynTagSerializer())
            contextual(IntArrayTag::class, DynTagSerializer())
            contextual(ListTag::class, DynTagSerializer())

            // Built in data classes
            contextual(BlockPos::class, BlockPosSerializer)
            contextual(Box::class, BoxSerializer)
        }

        val ReferenceSerializers = SerializersModule {
            contextual(Item::class, ItemRefSerializer)
        }

        val Default = NbtFormat(NbtFormatConfig())

    }

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> encodeToTag(serializer: SerializationStrategy<T>, obj: T): Tag {
        return when (serializer.descriptor.kind) {
            is PrimitiveKind -> {
                val enc = TaglessEncoder(config)
                enc.encodeSerializableValue(serializer, obj)
                enc.root
            }
            else -> {
                val enc = TagEncoder(config)
                enc.encodeSerializableValue(serializer, obj)
                enc.root
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> encodeToTag(obj: T) = encodeToTag(EmptySerializersModule.serializer(), obj)

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> decodeFromTag(deserializer: DeserializationStrategy<T>, tag: Tag): T {
        val decoder = when (tag) {
            is CompoundTag, is ListTag -> TagDecoder(config, 0, tag)
            else -> TaglessDecoder(config, 0, tag)
        }
        return decoder.decodeSerializableValue(deserializer)
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> decodeFromTag(tag: Tag) = decodeFromTag<T>(EmptySerializersModule.serializer(), tag)

}


