package io.ejekta.kambrik.internal.data

import io.ejekta.kambrik.serial.serializers.IdentitySer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.JsonElement
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier

object ServerDataRegistrar {

    data class DataRequest<T : Any>(val serializer: KSerializer<T>, val default: T) {
        val encoded: JsonElement
            get() = DefaultJsonFormat.encodeToJsonElement(serializer, default)

        fun decode(data: JsonElement): T {
            return DefaultJsonFormat.decodeFromJsonElement(serializer, data)
        }
    }

    private val requests = mutableMapOf<Identifier, DataRequest<*>>()

    var results = mutableMapOf<Identifier, JsonElement>()

    private val resultSerializer = MapSerializer(IdentitySer, JsonElement.serializer())

    val objects = mutableMapOf<Identifier, Any>()

    fun <T : Any> request(key: Identifier, serializer: KSerializer<T>, default: T) {
        requests[key] = DataRequest(serializer, default)
        println("Requested $key and serializer $serializer")
    }

    internal fun <R : Any> loadResult(key: Identifier): R {
        return objects[key] as R
    }

    internal fun <R : Any> setResult(key: Identifier, value: R) {
        objects[key] = value
    }

    fun loadResults(server: MinecraftServer) {
        val file = server.getFile("data.json")

        if (!file.exists()) {
            file.createNewFile()
            file.writeText(
                DefaultJsonFormat.encodeToString(resultSerializer, results)
            )
        }

        val contents = file.readText()

        results = DefaultJsonFormat.decodeFromString(resultSerializer, contents).toMutableMap()

        for ((reqId, reqData) in requests) {
            println("We got requested a: $reqId")
            val result = results.getOrPut(reqId) {
                reqData.encoded
            }
            val data = reqData.decode(result)
            objects[reqId] = data
        }


    }

}