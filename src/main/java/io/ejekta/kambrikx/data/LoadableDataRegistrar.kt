package io.ejekta.kambrikx.data

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.serial.serializers.IdentitySer
import io.ejekta.kambrik.serial.serializers.TextSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.io.File
import kotlin.reflect.full.starProjectedType

abstract class LoadableDataRegistrar {

    internal data class DataRequest<T : Any>(val serializer: KSerializer<T>, val default: T) {
        val encoded: JsonElement
            get() {
                println("This: $this")
                println("Ser: $serializer")
                println("Ser should be: ${TextSerializer}")
                println("think it's getting ${Kambrik.Serial.Format.serializersModule.getContextual(Text::class)}")
                return Kambrik.Serial.Format.encodeToJsonElement(serializer, default)
            }

        fun encode(value: Any?): JsonElement {
            return Kambrik.Serial.Format.encodeToJsonElement(serializer, value as? T ?: default)
        }

        fun decode(data: JsonElement): T {
            println("Decoding as: ${this::class.starProjectedType}")
            return Kambrik.Serial.Format.decodeFromJsonElement(serializer, data)
        }
    }

    protected abstract fun getFile(id: Identifier? = null): File

    protected abstract fun getRelatedObjects(id: Identifier): Map<Identifier, Any>

    private val requests = mutableMapOf<Identifier, DataRequest<*>>()

    private var results = mutableMapOf<Identifier, JsonElement>()

    private val resultSerializer = MapSerializer(IdentitySer, JsonElement.serializer())

    protected val loadedObjects = mutableMapOf<Identifier, Any>()

    protected val toFlushObjects = mutableMapOf<Identifier, Any>()

    open fun <T : Any> request(key: Identifier, serializer: KSerializer<T>, default: T) {
        println("Requested serializer: $serializer")
        requests[key] = DataRequest(serializer, default)
        //println("Requested $key and serializer $serializer")
    }

    open fun loadResults(id: Identifier) {
        val file = getFile(id)

        if (!file.exists()) {
            file.createNewFile()
            file.writeText(
                Kambrik.Serial.Format.encodeToString(resultSerializer, results)
            )
        }

        val contents = file.readText()

        results = Kambrik.Serial.Format.decodeFromString(resultSerializer, contents).toMutableMap()

        for ((reqId, reqData) in requests) {
            val result = results.getOrPut(reqId) {
                reqData.encoded
            }
            println("REQ: $reqId")
            println("DAT: $reqData")
            val data = reqData.decode(result)
            loadedObjects[reqId] = data
        }
    }

    open fun saveResults(id: Identifier) {
        val file = getFile(id)

        val outResults = mutableMapOf<Identifier, JsonElement>()

        // Only save objects in mod, if possible
        val toSave = getRelatedObjects(id)

        for ((objId, obj) in toSave) {
            val data = requests[objId]!!
            outResults[objId] = data.encode(obj)
        }

        val output = Kambrik.Serial.Format.encodeToString(resultSerializer, outResults)

        file.apply {
            if (!exists()) {
                createNewFile()
            }
            writeText(output)
        }
    }

    // These are only called via the delegate properties, so casting is guaranteed to succeed

    internal open fun <R : Any> loadResult(key: Identifier): R {
        return loadedObjects[key] as R
    }

    internal fun <R : Any> setResult(key: Identifier, value: R) {
        loadedObjects[key] = value
    }

}