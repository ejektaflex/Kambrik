@file:Suppress("EXPERIMENTAL_API_USAGE")
package io.ejekta.testing

import io.ejekta.kambrik.ext.wrapToPacketByteBuf
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.*
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import java.text.SimpleDateFormat
import java.util.*




fun <T> doJsonCycle(ser: KSerializer<T>, obj: T) {
    val json = Json {
        serializersModule = NbtFormat.Default.serializersModule
    }

    val c = json.encodeToString(ser, obj)
    println("OBJ -> JSN: $c")
    val d = json.decodeFromString(ser, c)
    println("JSN: -> OBJ: $d")
}

fun <T> doNbtCycle(ser: KSerializer<T>, obj: T) {
    val config = NbtFormat {
        nullTag = NbtString.of("NULL")
    }

    val a = config.encodeToTag(ser, obj)
    println("OBJ -> NBT: $a")
    val b = config.decodeFromTag(ser, a)
    println("NBT -> OBJ: $b")
}


@Serializable
data class InnerInt(val its: Int)

@Serializable
data class Doot(val pos: @Contextual Box, val int: InnerInt)


fun main(args: Array<String>) {

    val u = Doot(Box(1.0, 2.0, 3.0, 12.0, 15.0, 18.0), InnerInt(30))

    try {
        doJsonCycle(Doot.serializer(), u)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    try {
        doNbtCycle(Doot.serializer(), u)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

/*
    @Serializable
    data class Holder(val tag: @Contextual NbtElement)

    val t = NbtCompound().apply {
        putString("Hai", "There")
        putByte("Yo", 1)
        putByte("Ma", 3)
        put("Blah", LongArrayTag(longArrayOf(5L, 10L, 15L)))
    }

    val u = Holder(t)

    //val result = NbtFormat.Default.encodeToTag(NbtCompoundSerializer(), t)
    //*
    val result = NbtFormat.Default.encodeToTag(Holder.serializer(), u)
    println(result.toString())
    val result2 = NbtFormat.Default.decodeFromTag(Holder.serializer(), result)
    println(result2.toString())


    val json = Json {
        serializersModule = NbtFormat.BuiltInSerializers
    }

    val result3 = json.encodeToString(Holder.serializer(), u)
    println(result3)
    val result4 = json.decodeFromString(Holder.serializer(), result3)
    println(result4.toString())
*/

 */


