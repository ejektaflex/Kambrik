package io.ejekta.kambrikx.api.serial

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.KambrikExperimental
import io.ejekta.kambrikx.internal.serial.decoders.TagDecoder
import io.ejekta.kambrikx.internal.serial.decoders.TaglessDecoder
import io.ejekta.kambrikx.internal.serial.encoders.TagEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TaglessEncoder
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import net.minecraft.nbt.*

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
        include(Kambrik.Serial.DefaultSerializers)
        include(Kambrik.Serial.NbtSerializers)
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


