package io.ejekta.kambrik.ext.ksx

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
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



fun <U> createCodecFromKSerializer(serializer: KSerializer<U>): Codec<U> {
    val json = Json { encodeDefaults = true }

    return object : Codec<U> {

        override fun <T : Any> encode(input: U, ops: DynamicOps<T>, prefix: T): DataResult<T> {
            val jsonObject = json.encodeToJsonElement(serializer, input) as JsonObject
            val nbtMap = mutableMapOf<T, T>()

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
                nbtMap[ops.createString(key)] = dynamicValue
            }

            val doot = ops.createMap(nbtMap)
            return ops.mergeToPrimitive(prefix, doot)
        }

        override fun <T : Any?> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<U, T>> {
            val nbtMap = ops.getMap(input).result().getOrNull()

            TODO("Not yet implemented")
        }

//            nbtMap.
//
//            val jsonObject = JsonObject(nbtMap.mapValues { (_, dynamic) ->
//                val dynamicValue = dynamic.value()
//                when (ops.getStringValue(dynamicValue).result()) {
//                    is DataResult.Success -> JsonPrimitive(ops.getStringValue(dynamicValue).result().get())
//                    else -> when (val primitiveValue = dynamicValue.toString()) {
//                        is String -> JsonPrimitive(primitiveValue)
//                        else -> JsonPrimitive(primitiveValue)
//                    }
//                }
//            })
//            val result = json.decodeFromJsonElement(serializer, jsonObject)
//            return DataResult.success(result to input)

    }
}

@Serializable
class Doot(val name: String, val age: Int = 30)

fun main() {

    val DOOT_CODEC = createCodecFromKSerializer(Doot.serializer())

    val doot = Doot("Riebeck")

    val encodedElement = DOOT_CODEC.encodeStart(NbtOps.INSTANCE, doot)

    println(encodedElement)
    println(encodedElement.result().getOrNull())

}


class KotlinJsonCodec : Codec<JsonElement> {
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

//
//
//class KotlinSerializerCodec<C>(val serializer: KSerializer<C>) : MapCodec<JsonObject>() {
//    override fun <T : Any?> keys(ops: DynamicOps<T>): Stream<T> {
//        return ops.
//    }
//
//
//    override fun <T : Any?> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<JsonObject> {
//        input.
//    }
//
//
//    @OptIn(ExperimentalSerializationApi::class)
//    override fun <T : Any> encode(
//        input: JsonObject,
//        ops: DynamicOps<T>,
//        prefix: RecordBuilder<T>
//    ): RecordBuilder<T> {
//        var proto = prefix
//        val descriptor = serializer.descriptor
//        for (i in 0..<descriptor.elementsCount) {
//            val name = descriptor.getElementName(i)
//            val desc = descriptor.getElementDescriptor(i)
//            proto = when (desc.kind.toString()) {
//                "STRING" -> proto.add(name, Codec.STRING.encodeStart(ops, input[name]!!.jsonPrimitive.content))
//                "FLOAT" -> proto.add(name, Codec.FLOAT.encodeStart(ops, input[name]!!.jsonPrimitive.float))
//                "INT" -> proto.add(name, Codec.INT.encodeStart(ops, input[name]!!.jsonPrimitive.int))
//                else -> throw Exception("Unsupported json primitive type ${desc.kind}")
//            }
//        }
//        return proto
//    }
//}
//
//class KotlinSerializerCodecNonMap<C>(val serializer: KSerializer<C>) : Decoder<JsonObject> {
//
//    @OptIn(ExperimentalSerializationApi::class)
//    override fun <T : Any?> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<JsonObject, T>> {
//        var proto = ops.mapBuilder()
//        val descriptor = serializer.descriptor
//        for (i in 0..<descriptor.elementsCount) {
//            val name = descriptor.getElementName(i)
//            val desc = descriptor.getElementDescriptor(i)
//            proto = when (desc.kind.toString()) {
//                "STRING" -> Codec.STRING.decode(ops., input)
//                "FLOAT" -> proto.add(name, Codec.FLOAT.encodeStart(ops, input[name]!!.jsonPrimitive.float))
//                "INT" -> proto.add(name, Codec.INT.encodeStart(ops, input[name]!!.jsonPrimitive.int))
//                else -> throw Exception("Unsupported json primitive type ${desc.kind}")
//            }
//        }
//        return proto.build(input)
//    }
//
//    @OptIn(ExperimentalSerializationApi::class)
//    override fun <T : Any> encode(input: JsonObject, ops: DynamicOps<T>, prefix: T): DataResult<T> {
//        var proto = ops.mapBuilder()
//        val descriptor = serializer.descriptor
//        for (i in 0..<descriptor.elementsCount) {
//            val name = descriptor.getElementName(i)
//            val desc = descriptor.getElementDescriptor(i)
//            proto = when (desc.kind.toString()) {
//                "STRING" -> proto.add(name, Codec.STRING.encodeStart(ops, input[name]!!.jsonPrimitive.content))
//                "FLOAT" -> proto.add(name, Codec.FLOAT.encodeStart(ops, input[name]!!.jsonPrimitive.float))
//                "INT" -> proto.add(name, Codec.INT.encodeStart(ops, input[name]!!.jsonPrimitive.int))
//                else -> throw Exception("Unsupported json primitive type ${desc.kind}")
//            }
//        }
//        return proto.build(prefix)
//    }
//
//
//}
//
//@OptIn(ExperimentalSerializationApi::class)
//fun <T> codecFromSerializer(serializer: KSerializer<T>): Codec<T> {
//    val descriptor = serializer.descriptor
//    val subCodecs = mutableListOf<RecordCodecBuilder<JsonObject, String>>()
//    for (i in 0..<descriptor.elementsCount) {
//        val name = descriptor.getElementName(i)
//        val desc = descriptor.getElementDescriptor(i)
//        when (desc.kind.toString()) {
//            "STRING" -> subCodecs.add(Codec.STRING.fieldOf(name).forGetter { jo: JsonObject ->
//                jo[name]!!.jsonPrimitive.content
//            })
////            "FLOAT" -> subCodecs.add(Codec.FLOAT.fieldOf(name))
////            "INT" -> subCodecs.add(Codec.INT.fieldOf(name))
//        }
//    }
//
//    // Codec.of(SinglePoolElement::encodeLocation, Identifier.CODEC.map(Either::left));
//
//    RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<JsonObject> ->
//
//        var product = subCodecs.first()
//
//
//
//        instance.group(Codec.STRING.fieldOf("test").forGetter { je: JsonElement ->
//            je.jsonPrimitive.content
//        }).and(
//            Codec.STRING.fieldOf("test").forGetter { je: JsonElement ->
//                je.jsonPrimitive.content
//            }
//        )
//
//        instance.group(Codec.STRING.fieldOf("test").forGetter { je: JsonElement ->
//            je.jsonPrimitive.content
//        }).apply(instance) { jo ->
//            buildJsonObject {  }
//        }
//    }
//
//    return Codec.of(descriptor, subCodecs)
//}


