package io.ejekta.percale.reverse

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import io.ejekta.percale.decoder.PassDecoder
import io.ejekta.percale.encoder.PassEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
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

object GsonElementSerializer : KSerializer<JsonElement> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    // Even if NBT won't use this, it's useful for JsonOps and such
    override val descriptor: SerialDescriptor = buildSerialDescriptor("percale.NbtElement", PolymorphicKind.OPEN) {
        element("percale.GsonPrimitiveString", GsonStringSerializer.descriptor)
        element("percale.GsonPrimitiveInt", GsonIntSerializer.descriptor)
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
                            else -> throw Exception("nuuuu")
                        }
                    }
                    else -> throw Exception("nu")
                }
                if (input.isString) {
                    GsonStringSerializer
                } else {
                    throw Exception("Nooo")
                }
            }
            else -> throw Exception("NbtElementSerializer does not know what serializer to use for this type: ${input::class.simpleName}")
            //...etc
        }
        return ser as KSerializer<JsonElement>
    }
}