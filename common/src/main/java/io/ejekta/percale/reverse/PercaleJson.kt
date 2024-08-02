package io.ejekta.percale.reverse

import com.google.gson.JsonParser
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import io.ejekta.percale.deserialize
import io.ejekta.percale.serialize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import com.google.gson.JsonElement as GsonElement

open class PercaleJson(val ops: DynamicOps<GsonElement>, val json: Json) {
    fun <A : Any> dynamicEncodeToJsonElement(obj: A, serializer: KSerializer<A>): JsonElement {
        val gsonEncoded = ops.serialize(obj, serializer, json.serializersModule)
        val jsonEncoded = json.decodeFromString(JsonElement.serializer(), gsonEncoded.toString())
        return jsonEncoded
    }

    fun <A : Any> dynamicEncodeToString(obj: A, serializer: KSerializer<A>): String {
        return json.encodeToString(JsonElement.serializer(), dynamicEncodeToJsonElement(obj, serializer))
    }

    fun <A : Any> dynamicDecodeFromJsonElement(element: GsonElement, serializer: KSerializer<A>): A {
        return ops.deserialize(element, serializer, json.serializersModule)
    }

    fun <A : Any> dynamicDecodeFromString(str: String, serializer: KSerializer<A>): A {
        val gsonParsed = JsonParser.parseString(str)
        return dynamicDecodeFromJsonElement(gsonParsed, serializer)
    }

    companion object : PercaleJson(JsonOps.INSTANCE, Json.Default) {
        val Default = this
    }
}