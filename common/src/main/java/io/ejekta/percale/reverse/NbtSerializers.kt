package io.ejekta.percale.reverse

import io.ejekta.kambrik.ext.toMap
import io.ejekta.percale.decoder.PassDecoder
import io.ejekta.percale.encoder.PassEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import net.minecraft.nbt.*

object NbtStringSerializer : KSerializer<NbtString> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("percale.NbtString", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: NbtString) {
        encoder.encodeString(value.asString())
    }
    override fun deserialize(decoder: Decoder): NbtString {
        return NbtString.of(decoder.decodeString())
    }
}

object NbtIntSerializer : KSerializer<NbtInt> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("percale.NbtInt", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: NbtInt) {
        encoder.encodeInt(value.intValue())
    }

    override fun deserialize(decoder: Decoder): NbtInt {
        return NbtInt.of(decoder.decodeInt())
    }
}


object NbtCompoundSerializer : KSerializer<NbtCompound> {
    private val ser = MapSerializer(String.serializer(), NbtElementSerializer)
    override val descriptor: SerialDescriptor = ser.descriptor
    override fun serialize(encoder: Encoder, value: NbtCompound) {
        encoder.encodeSerializableValue(ser, value.toMap())
    }

    override fun deserialize(decoder: Decoder): NbtCompound {
        val nbtMap = decoder.decodeSerializableValue(ser)
        val baseCompound = NbtCompound()
        for ((key, value) in nbtMap) {
            baseCompound.put(key, value)
        }
        return baseCompound
    }
}

//class NewNbtElementSerializer : KSerializer<NbtElement>

fun doot() {
    JsonElement
    Int.serializer()
}

object NbtElementSerializer : KSerializer<NbtElement> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    // Even if NBT won't use this, it's useful for JsonOps and such
    override val descriptor: SerialDescriptor = buildSerialDescriptor("percale.NbtElement", PolymorphicKind.OPEN) {
        element("percale.NbtInt", NbtIntSerializer.descriptor)
        element("percale.NbtString", NbtStringSerializer.descriptor)
        //...etc
    }

    override fun serialize(encoder: Encoder, value: NbtElement) {
        if (encoder is PassEncoder<*> && encoder.ops is NbtOps) {
            val ser = fromInput(value)
            return encoder.encodeSerializableValue(ser, value)
        }
        return encoder.encodeSerializableValue(PolymorphicSerializer(NbtElement::class), value)
    }

    override fun deserialize(decoder: Decoder): NbtElement {
        // If not an NBT pass decoder, then this could be an NbtElement being serialized by JsonOps! handle normally in that instance
        val pass = decoder as? PassDecoder<*> ?: return decoder.decodeSerializableValue(PolymorphicSerializer(NbtElement::class))
        val inp = pass.input as NbtElement
        val deser = fromInput(inp)
        return pass.decodeSerializableValue(deser, inp)
    }

    fun fromInput(input: NbtElement): KSerializer<NbtElement> {
        val ser =  when (input) {
            is NbtString -> NbtStringSerializer
            is NbtInt -> NbtIntSerializer
            else -> throw Exception("NbtElementSerializer does not know what serializer to use for this type: ${input.nbtType}")
            //...etc
        }
        return ser as KSerializer<NbtElement>
    }
}

//object NbtElementSerializer : KSerializer<NbtElement> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("blah", PrimitiveKind.STRING)
//    override fun serialize(encoder: Encoder, value: NbtElement) {
//        when (value) {
//            is NbtCompound -> NbtCompoundSerializer.serialize(encoder, value)
//            is NbtInt -> NbtIntSerializer.serialize(encoder, value)
//            is NbtString -> NbtStringSerializer.serialize(encoder, value)
//            /*
//            is NbtList -> {
//                val listSerializer = ListSerializer(this)
//                encoder.encodeSerializableValue(listSerializer, value.toList())
//            }
//             */
//            else -> throw Exception("Bad NBT Element value type!")
//        }
//    }
//
//    override fun deserialize(decoder: Decoder): NbtElement {
//        println("Deser..")
//        println(decoder.serializersModule)
//        println(descriptor)
//
//        return TODO("please implement this")
//    }
//}



