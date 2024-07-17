package io.ejekta.kambrikx.serial

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.Lifecycle
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.percale.*
import io.ejekta.percale.encoder.PassEncoder
import io.ejekta.percale.reverse.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.minecraft.block.Block
import net.minecraft.nbt.*
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import net.minecraft.util.Identifier
import net.minecraft.util.Uuids
import net.minecraft.util.dynamic.Codecs
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import java.awt.Color
import java.time.Instant
import java.util.*

fun <M : KambrikMsg> KSerializer<M>.toSimplePacketCodec(): PacketCodec<RegistryByteBuf, M> {
    val json = INetworkLink.defaultJson
    return PacketCodec.of(
        { value, buf -> buf.writeString(json.encodeToString(this, value)) },
        { json.decodeFromString(this, it.readString()) }
    )
}

@OptIn(ExperimentalSerializationApi::class)
fun main() {

    @Serializable
    data class Hobby(val name: String)

    @Serializable
    data class Doot(val amount: Int, val where: @Contextual NbtCompound)

    val ops = JsonOps.INSTANCE

    val serMod = SerializersModule {
        contextualCodec(Identifier.CODEC)
        contextualCodec(BlockPos.CODEC)
        contextualCodec(Codecs.INSTANT)
        contextualCodec(Codecs.MATRIX4F)
        contextual(NbtElementSerializer)
        contextual(NbtCompoundSerializer)
        contextual(NbtLongSerializer)
        contextual(NbtIntSerializer)
    }

    val jsonFormat = PercaleJson(JsonOps.INSTANCE, Json {
        prettyPrint = true
        serializersModule = serMod
    })

    val ser = Doot.serializer()

    val item = Doot(100, NbtCompound().apply {
        putLong("ey", 3L)
    })

    val doot = ops.serialize(
        item,
        ser,
        serialMod = serMod
    )

    println(doot)

    val dootTwo = jsonFormat.dynamicEncodeToString(
        item,
        ser
    )

    println(dootTwo)


//    println("### A ###")
//    println(
//        ops.serialize(item, serializer, serMod)
//    )
//
//    println("### B ###")
//    println(
//        jsonFormat.dynamicEncodeToString(item, serializer)
//    )

//    val agnosticSerializer = Doot.serializer()
//
//    val item = Doot(100, Identifier.of("a", "b"))
//
//    val result = jsonFormat.dynamicEncodeToString(item, agnosticSerializer)
//
//    println(result)


//    val doot = ops.serialize(
//        item, agnosticSerializer
//    )
//
//    println(doot)

    //val back = ops.deserialize<JsonElement, NbtCompound>(doot!!, agnosticSerializer)



}