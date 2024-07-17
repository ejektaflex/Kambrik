package io.ejekta.percale.reverse

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import io.ejekta.percale.decoder.PassDecoder
import io.ejekta.percale.encoder.PassEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.NbtOps

object GsonStringSerializer : KSerializer<JsonPrimitive> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("percale.GsonPrimitiveString", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: JsonPrimitive) {
        encoder.encodeString(value.asString)
    }
    override fun deserialize(decoder: Decoder): JsonPrimitive {
        return JsonPrimitive(decoder.decodeString())
    }
}

object GsonIntSerializer : KSerializer<JsonPrimitive> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("percale.GsonPrimitiveInt", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: JsonPrimitive) {
        encoder.encodeInt(value.asInt)
    }
    override fun deserialize(decoder: Decoder): JsonPrimitive {
        return JsonPrimitive(decoder.decodeInt())
    }
}

object GsonLongSerializer : KSerializer<JsonPrimitive> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("percale.GsonPrimitiveLong", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: JsonPrimitive) {
        encoder.encodeLong(value.asLong)
    }
    override fun deserialize(decoder: Decoder): JsonPrimitive {
        return JsonPrimitive(decoder.decodeLong())
    }
}

object GsonFloatSerializer : KSerializer<JsonPrimitive> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("percale.GsonPrimitiveFloat", PrimitiveKind.FLOAT)
    override fun serialize(encoder: Encoder, value: JsonPrimitive) {
        encoder.encodeFloat(value.asFloat)
    }
    override fun deserialize(decoder: Decoder): JsonPrimitive {
        return JsonPrimitive(decoder.decodeFloat())
    }
}

object GsonObjectSerializer : KSerializer<JsonObject> {
    private val ser
        get() = MapSerializer(String.serializer(), GsonElementSerializer)
    override val descriptor: SerialDescriptor = deferred { ser.descriptor }
    override fun serialize(encoder: Encoder, value: JsonObject) {
        encoder.encodeSerializableValue(ser, value.asMap())
    }
    override fun deserialize(decoder: Decoder): JsonObject {
        val jsonMap = decoder.decodeSerializableValue(ser)
        return JsonObject().apply {
            for ((key, value) in jsonMap) {
                add(key, value)
            }
        }
    }
}

object GsonArraySerializer : KSerializer<JsonArray> {
    private val ser
        get() = ListSerializer(GsonElementSerializer)
    override val descriptor: SerialDescriptor = deferred { ser.descriptor }
    override fun serialize(encoder: Encoder, value: JsonArray) {
        encoder.encodeSerializableValue(ser, value.asList())
    }
    override fun deserialize(decoder: Decoder): JsonArray {
        val jsonList = decoder.decodeSerializableValue(ser)
        return JsonArray().apply {
            for (item in jsonList) {
                add(item)
            }
        }
    }
}


object GsonElementSerializer : KSerializer<JsonElement> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    // Even if NBT won't use this, it's useful for JsonOps and such
    override val descriptor: SerialDescriptor = buildSerialDescriptor("percale.NbtElement", PolymorphicKind.OPEN) {
        element("percale.GsonPrimitiveString", GsonStringSerializer.descriptor)
        element("percale.GsonPrimitiveInt", GsonIntSerializer.descriptor)
        element("percale.GsonPrimitiveLong", GsonLongSerializer.descriptor)
        element("percale.GsonPrimitiveFloat", GsonFloatSerializer.descriptor)
        element("percale.GsonObject", GsonObjectSerializer.descriptor)
        element("percale.GsonArray", GsonArraySerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: JsonElement) {
        if (encoder is PassEncoder<*> && (encoder.ops is NbtOps || encoder.ops is JsonOps)) {
            val ser = fromInput(value)
            return encoder.encodeSerializableValue(ser, value)
        }
        return encoder.encodeSerializableValue(PolymorphicSerializer(JsonElement::class), value)
    }

    override fun deserialize(decoder: Decoder): JsonElement {
        // If not an NBT pass decoder, then this could be an NbtElement being serialized by JsonOps! handle normally in that instance
        val pass = decoder as? PassDecoder<*> ?: return decoder.decodeSerializableValue(PolymorphicSerializer(JsonElement::class))
        val inp = pass.input as JsonElement
        val deser = fromInput(inp)
        return pass.decodeSerializableValue(deser, inp)
    }

    fun fromInput(input: JsonElement): KSerializer<JsonElement> {
        val ser =  when (input) {
            is JsonPrimitive -> {
                when (true) {
                    input.isString -> GsonStringSerializer
                    input.isNumber -> {
                        when (input.asNumber) {
                            is Int -> GsonIntSerializer
                            is Long -> GsonLongSerializer
                            is Float -> GsonFloatSerializer
                            else -> throw Exception("Err: bad inpu: $input was not a known number format")
                        }
                    }
                    else -> throw Exception("nu")
                }
            }
            is JsonObject -> GsonObjectSerializer
            is JsonArray -> GsonArraySerializer
            else -> throw Exception("GsonElementSerializer does not know what serializer to use for this type: ${input::class.simpleName}")
            //...etc
        }
        return ser as KSerializer<JsonElement>
    }
}