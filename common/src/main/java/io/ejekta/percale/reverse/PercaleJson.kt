package io.ejekta.percale.reverse

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import io.ejekta.percale.deserialize
import io.ejekta.percale.serialize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

open class PercaleJson(val ops: JsonOps, val json: Json) {
    fun <A : Any> dynamicEncodeToJsonElement(obj: A, serializer: KSerializer<A>): JsonElement {
        val gsonEncoded = ops.serialize(obj, serializer, json.serializersModule)
        val jsonEncoded = json.decodeFromString(JsonElement.serializer(), gsonEncoded.toString())
        return jsonEncoded
    }

    fun <A : Any> dynamicEncodeToString(obj: A, serializer: KSerializer<A>): String {
        return json.encodeToString(JsonElement.serializer(), dynamicEncodeToJsonElement(obj, serializer))
    }

    fun <A : Any> dynamicDecodeFromJsonElement(element: JsonElement, serializer: KSerializer<A>): A {
        val gsonParsed = JsonParser.parseString(element.toString())
        return ops.deserialize(gsonParsed, serializer)
    }

    companion object : PercaleJson(JsonOps.INSTANCE, Json.Default) {
        val Default = this
    }
}