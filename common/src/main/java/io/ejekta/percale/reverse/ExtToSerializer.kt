package io.ejekta.percale.reverse

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import io.ejekta.kambrik.ext.toMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import net.minecraft.nbt.NbtByte
import net.minecraft.nbt.NbtByteArray
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtString
import net.minecraft.nbt.StringNbtReader
import com.google.gson.JsonElement as GsonElement
import com.google.gson.JsonParser as GsonParser


// This is gross but ok
class GsonElementSerializer(val json: Json) : KSerializer<GsonElement> {
    val jsonSer = JsonElement.serializer()
    override val descriptor: SerialDescriptor = jsonSer.descriptor
    override fun serialize(encoder: Encoder, value: GsonElement) {
        encoder.encodeSerializableValue(jsonSer, json.decodeFromString(jsonSer, value.toString()))
    }

    override fun deserialize(decoder: Decoder): GsonElement {
        val result = decoder.decodeSerializableValue(jsonSer)
        return GsonParser.parseString(result.toString())
    }
}

fun <A> Codec<A>.toKotlinJsonSerializer(json: Json = Json.Default): KSerializer<A> {
    return toGenericSer(JsonOps.INSTANCE, GsonElementSerializer(json))
}

fun <A> Codec<A>.toKotlinNbtSerializer(): KSerializer<A> {
    return toGenericSer(NbtOps.INSTANCE, NbtElementSerializer)
}

fun <A> Codec<A>.toWrappedJsonSerializer(ops: DynamicOps<GsonElement>, json: Json = Json.Default): KSerializer<A> {
    return toGenericSer(ops, GsonElementSerializer(json))
}

fun <A, U> Codec<A>.toGenericSer(ops: DynamicOps<U>, opsSerializer: KSerializer<U>): KSerializer<A> {
    return object : KSerializer<A> {
        override val descriptor: SerialDescriptor
            get() = opsSerializer.descriptor // I suppose this works

        override fun serialize(encoder: Encoder, value: A) {
            val result = this@toGenericSer.encodeStart(ops, value).result().get()
            encoder.encodeSerializableValue(opsSerializer, result)
        }

        override fun deserialize(decoder: Decoder): A {
            val result = decoder.decodeSerializableValue(opsSerializer)
            return this@toGenericSer.parse(ops, result).result().get()
        }
    }
}


