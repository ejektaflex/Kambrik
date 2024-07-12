import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.contextual
import percale.encoder.PassEncoder
import java.util.stream.Stream

// This is gross but ok
class GsonElementSerializer(serializersModule: SerializersModule) : KSerializer<JsonElement> {
    val json = Json {
        this.serializersModule = serializersModule
    }
    val jsonSer = kotlinx.serialization.json.JsonElement.serializer()
    override val descriptor: SerialDescriptor = jsonSer.descriptor
    override fun serialize(encoder: Encoder, value: JsonElement) {
        encoder.encodeSerializableValue(jsonSer, json.decodeFromString(jsonSer, value.toString()))
    }

    override fun deserialize(decoder: Decoder): JsonElement {
        val result = decoder.decodeSerializableValue(jsonSer)
        return JsonParser.parseString(result.toString())
    }
}

inline fun <reified A : Any> SerializersModuleBuilder.codec(codec: Codec<A>) {
    contextual(codec.toKotlinJsonSerializer())
}

fun <A> Codec<A>.toKotlinJsonSerializer(serializersModule: SerializersModule = EmptySerializersModule()): KSerializer<A> {
    return toGenericSer(JsonOps.INSTANCE, GsonElementSerializer(serializersModule))
}

fun <A, U> Codec<A>.toGenericSer(ops: DynamicOps<U>, opsSerializer: KSerializer<U>): KSerializer<A> {
    return object : KSerializer<A> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("test", PrimitiveKind.INT)

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