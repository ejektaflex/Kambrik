package io.ejekta.kambrikx.api.serial

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.KambrikExperimental
import io.ejekta.kambrik.api.serial.serializers.*
import io.ejekta.kambrikx.internal.serial.decoders.TagDecoder
import io.ejekta.kambrikx.internal.serial.decoders.TaglessDecoder
import io.ejekta.kambrikx.internal.serial.encoders.TagEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TaglessEncoder
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

@KambrikExperimental
@OptIn(InternalSerializationApi::class)
class NbtFormatConfig {

    //private val nbtEncodingMarker = Kambrik.Logging.createMarker(KambrikMod.idOf("nbt"))

    private val logger = Kambrik.Logger

    var showDebug = false

    internal fun logInfo(level: Int, msg: String) {
        if (showDebug) {
            //logger.info(nbtEncodingMarker, "\t".repeat(level) + msg)
        }
    }

    var classDiscriminator: String = "type"

    @ExperimentalSerializationApi
    var serializersModule: SerializersModule = SerializersModule {
        include(NbtFormat.BuiltInSerializers)
        include(NbtFormat.ReferenceSerializers)
    }

    var writePolymorphic = true

    var nullTag: NbtElement = NbtString.of("\$NULL")

    var encodeDefault = false

}

@KambrikExperimental
@OptIn(InternalSerializationApi::class)
open class NbtFormat internal constructor(val config: NbtFormatConfig) : SerialFormat {

    constructor(configFunc: NbtFormatConfig.() -> Unit) : this(NbtFormatConfig().apply(configFunc))

    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule = EmptySerializersModule + config.serializersModule

    companion object {

        @Suppress("UNCHECKED_CAST")
        val BuiltInSerializers = SerializersModule {

            // Polymorphic

            // Contextual

            // Tags
            contextual(NbtElement::class, DynTagSerializer)
            contextual(NbtCompound::class, DynTagSerializer())
            contextual(NbtInt::class, DynTagSerializer())
            contextual(NbtString::class, DynTagSerializer())
            contextual(NbtDouble::class, DynTagSerializer())
            contextual(NbtByte::class, DynTagSerializer())
            contextual(NbtFloat::class, DynTagSerializer())
            contextual(NbtLong::class, DynTagSerializer())
            contextual(NbtShort::class, DynTagSerializer())

            // Complex Tags
            contextual(NbtLongArray::class, DynTagSerializer())
            contextual(NbtIntArray::class, DynTagSerializer())
            contextual(NbtList::class, DynTagSerializer())

            // Built in data classes
            contextual(BlockPos::class, BlockPosSerializer)
            contextual(Box::class, BoxSerializer)
        }

        val ReferenceSerializers = SerializersModule {
            contextual(Item::class, ItemRefSerializer)
            contextual(Block::class, BlockRefSerializer)
        }

        val Default = NbtFormat(NbtFormatConfig())

    }

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> encodeToTag(serializer: SerializationStrategy<T>, obj: T): NbtElement {
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
    fun <T> decodeFromTag(deserializer: DeserializationStrategy<T>, tag: NbtElement): T {
        val decoder = when (tag) {
            is NbtCompound, is NbtList -> TagDecoder(config, 0, tag)
            else -> TaglessDecoder(config, 0, tag)
        }
        return decoder.decodeSerializableValue(deserializer)
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> decodeFromTag(tag: NbtElement) = decodeFromTag<T>(EmptySerializersModule.serializer(), tag)

}


