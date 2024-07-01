package io.ejekta.kambrik.ext.ksx

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.capturedKClass
import kotlinx.serialization.json.*
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtString
import kotlin.jvm.optionals.getOrNull

fun <T> Json.encodeToStringTag(serializer: KSerializer<T>, value: T): NbtString {
    return NbtString.of(encodeToString(serializer, value))
}

fun <T> Json.decodeFromStringTag(serializer: KSerializer<T>, nbtString: NbtString): T {
    return decodeFromString(serializer, nbtString.asString())
}

@OptIn(InternalSerializationApi::class)
fun <T> KSerializer<T>.codec(): Codec<T> {
    return createCodecFromKSerializer(this)
}

class KotlinJsonObjectCodec<U>(val serializer: KSerializer<U>, val json: Json) : Codec<JsonObject> {
    val descriptor = serializer.descriptor

    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override fun <T : Any> encode(input: JsonObject, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        val dataMap = mutableMapOf<T, T>()

        for (elementIndex in 0..<descriptor.elementsCount) {
            val elDesc = descriptor.getElementDescriptor(elementIndex)
            val key = descriptor.getElementName(elementIndex)
            val result = when (elDesc.serialName) {
                "kotlin.String" -> {
                    val value = input[key]!!.jsonPrimitive.contentOrNull
                    value?.let { ops.createString(it) }
                }
                "kotlin.Int" -> {
                    val value = input[key]!!.jsonPrimitive.intOrNull
                    value?.let { ops.createInt(it) }
                }
                "kotlin.Double" -> {
                    val value = input[key]!!.jsonPrimitive.doubleOrNull
                    value?.let { ops.createDouble(it) }
                }
                else -> {
                    println("Error!!!")

                    // Now we resort to Contextual, and then Polymorphic, lookups. We finally fall back to a class load and check for it's serializer
                    // TODO add Contextual and Polymorphic lookup before resorting to class loading for serializer fetch
                    // TODO fetch class loadable serialnames into a map to avoid perf hits?
                    val clazz = Class.forName(elDesc.serialName, false, ClassLoader.getSystemClassLoader()).kotlin

                    println(clazz)
                    println(clazz.serializerOrNull())

                    val subObjectCodec = KotlinJsonObjectCodec(clazz.serializer(), json)

                    println(subObjectCodec)

                    // We naively assume that a class will instantly map to an object and not, say, an array or anything else
                    val thingy = subObjectCodec.encodeStart(ops, input[key]!!.jsonObject)

                    thingy.result().get()
                }
            }
            result?.let { dataMap[ops.createString(key)] = it }
        }

        val doot = ops.createMap(dataMap)
        return ops.mergeToPrimitive(prefix, doot)
    }

    override fun <T : Any> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<JsonObject, T>> {
        TODO("Not yet implemented")
    }

}


@OptIn(ExperimentalSerializationApi::class)
fun <U> createCodecFromKSerializer(serializer: KSerializer<U>): Codec<U> {
    val json = Json { encodeDefaults = true }

    return object : Codec<U> {

        override fun <T : Any> encode(input: U, ops: DynamicOps<T>, prefix: T): DataResult<T> {
            val jsonObject = json.encodeToJsonElement(serializer, input) as JsonObject
            val dataMap = mutableMapOf<T, T>()

            // TODO iterating the serializer kinds could provide us with better typing than relying on the primitives,
            // for better NBT interop

            jsonObject.forEach { (key, value) ->

                val dynamicValue = when (val jsonPrimitive = value.jsonPrimitive) {
                    is JsonPrimitive -> when {
                        jsonPrimitive.isString -> ops.createString(jsonPrimitive.content)
                        jsonPrimitive.intOrNull != null -> ops.createInt(jsonPrimitive.int)
                        jsonPrimitive.booleanOrNull != null -> ops.createBoolean(jsonPrimitive.boolean)
                        jsonPrimitive.doubleOrNull != null -> ops.createDouble(jsonPrimitive.double)
                        else -> ops.createString(jsonPrimitive.content)
                    }
                    else -> ops.createString(value.toString())
                }
                dataMap[ops.createString(key)] = dynamicValue
            }

            val doot = ops.createMap(dataMap)
            return ops.mergeToPrimitive(prefix, doot)
        }

        override fun <T : Any> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<U, T>> {
            val nbtMap = ops.getMap(input).result().getOrNull() ?: return DataResult.error { "Not a maplike!" }
            val descriptor = serializer.descriptor

            val jsonObject = buildJsonObject {
                for (entry in nbtMap.entries()) {
                    println(entry)

                    val key = ops.getStringValue(entry.first).result().get()
                    val kind = descriptor.getElementDescriptor(descriptor.getElementIndex(key))
                    println(kind.capturedKClass)
                    println("CLASS CHK:")
                    println(Class.forName(kind.serialName))
                    when (val kindName: String = kind.serialName) {
                        "kotlin.String" -> put(key, ops.getStringValue(entry.second).result().get())
                        "kotlin.Int" -> put(key, ops.getNumberValue(entry.second).result().get().toInt())
                        "kotlin.Double" -> put(key, ops.getNumberValue(entry.second).result().get().toDouble())
                        else -> throw Exception("Could not decode key '$key' with serialKind '$kindName'!")
                    }
                }
            }


            val result = json.decodeFromJsonElement(serializer, jsonObject)

            return DataResult.success(Pair.of(result, ops.empty()))
        }

    }
}

@Serializable
data class JobWork(val title: String)

@Serializable
data class Person(val name: String, val age: Int = 30, val cash: Double, val jobWork: JobWork = JobWork("No Job"))

fun main() {
    testObject()
}

fun testObject() {
    println("INITIAL:")

    val MY_CODEC = KotlinJsonObjectCodec(Person.serializer(), Json)

    val riebeck = Person("Riebeck", 36, 20.0, JobWork("Salesman"))

    val riebeckJson = Json.encodeToJsonElement(riebeck) as JsonObject

    println("\nENCODED:")

    val riebeckNbt = MY_CODEC.encodeStart(NbtOps.INSTANCE, riebeckJson)

    println(riebeckNbt)
}

fun testNormal() {
    println("INITIAL:")

    val PERSON_CODEC = Person.serializer().codec() // Auto-generate codec from Serializable object

    val riebeck = Person("Riebeck", 36, 20.0, JobWork("Salesman"))

    println(riebeck)

    println("\nENCODING:")

    val encodedElement = PERSON_CODEC.encodeStart(NbtOps.INSTANCE, riebeck)
    println(encodedElement)
    val result = encodedElement.result().getOrNull()
    println(result)

    println("\nDECODING:")

    val decodedElement = PERSON_CODEC.parse(NbtOps.INSTANCE, result)

    println(decodedElement)
    println(decodedElement.result().getOrNull())
}


class KotlinJsonCodecLegacySimpleProof : Codec<JsonElement> {
    override fun <T : Any> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<JsonElement, T>> {
        return when (input::class) {
            String::class -> DataResult.success(Pair(JsonPrimitive(input as String), input))
            Float::class -> DataResult.success(Pair(JsonPrimitive(input as Float), input))
            Int::class -> DataResult.success(Pair(JsonPrimitive(input as Int), input))
            Boolean::class -> DataResult.success(Pair(JsonPrimitive(input as Boolean), input))
            else -> throw Exception("Unsupported conversion for ${input::class}")
        }
    }

    override fun <T : Any> encode(input: JsonElement, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        return when (input) {
            is JsonPrimitive -> when {
                input.jsonPrimitive.isString -> Codec.STRING.encode(input.content, ops, prefix)
                input.jsonPrimitive.booleanOrNull != null -> Codec.BOOL.encode(input.boolean, ops, prefix)
                input.jsonPrimitive.floatOrNull != null -> Codec.FLOAT.encode(input.float, ops, prefix)
                input.jsonPrimitive.intOrNull != null -> Codec.INT.encode(input.int, ops, prefix)
                else -> throw Exception("Unsupported json primitive type ${input.jsonPrimitive}")
            }
            else -> throw Exception("Unsupported json type ${input.jsonPrimitive}")
        }
    }
}


