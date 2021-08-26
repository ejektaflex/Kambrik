package io.ejekta.kambrikx.data

import io.ejekta.kambrik.serial.serializers.IdentitySer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.JsonElement
import net.minecraft.util.Identifier
import java.io.File

internal abstract class LoadableDataRegistrar {

    internal data class DataRequest<T : Any>(val serializer: KSerializer<T>, val default: T) {
        val encoded: JsonElement
            get() = DefaultJsonFormat.encodeToJsonElement(serializer, default)

        fun encode(value: Any?): JsonElement {
            return DefaultJsonFormat.encodeToJsonElement(serializer, value as? T ?: default)
        }

        fun decode(data: JsonElement): T {
            return DefaultJsonFormat.decodeFromJsonElement(serializer, data)
        }
    }

    protected abstract fun getFile(id: Identifier? = null): File

    private val requests = mutableMapOf<Identifier, DataRequest<*>>()

    private var results = mutableMapOf<Identifier, JsonElement>()

    private val resultSerializer = MapSerializer(IdentitySer, JsonElement.serializer())

    private val loadedObjects = mutableMapOf<Identifier, Any>()

    protected val toFlushObjects = mutableMapOf<Identifier, Any>()

    fun <T : Any> request(key: Identifier, serializer: KSerializer<T>, default: T) {
        requests[key] = DataRequest(serializer, default)
        //println("Requested $key and serializer $serializer")
    }

    open fun loadResults() {
        val file = getFile()

        if (!file.exists()) {
            file.createNewFile()
            file.writeText(
                DefaultJsonFormat.encodeToString(resultSerializer, results)
            )
        }

        val contents = file.readText()

        results = DefaultJsonFormat.decodeFromString(resultSerializer, contents).toMutableMap()

        for ((reqId, reqData) in requests) {
            val result = results.getOrPut(reqId) {
                reqData.encoded
            }
            val data = reqData.decode(result)
            loadedObjects[reqId] = data
        }
    }

    open fun saveResults() {
        val file = getFile()

        val outResults = mutableMapOf<Identifier, JsonElement>()

        for ((objId, obj) in loadedObjects) {
            val data = requests[objId]!!
            outResults[objId] = data.encode(obj)
        }

        val output = DefaultJsonFormat.encodeToString(resultSerializer, outResults)

        file.apply {
            if (!exists()) {
                createNewFile()
            }
            writeText(output)
        }
    }

    internal open fun <R : Any> loadResult(key: Identifier): R {
        return loadedObjects[key] as R
    }

    internal fun <R : Any> setResult(key: Identifier, value: R) {
        loadedObjects[key] = value
    }

}