@file:Suppress("EXPERIMENTAL_API_USAGE")
package io.ejekta.testing

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.KambrikExperimental
import io.ejekta.kambrik.serial.serializers.TextSerializer
import io.ejekta.kambrik.text.textLiteral
//import io.ejekta.kambrik.serial.serializers.TextSerializer
import io.ejekta.kambrikx.serial.NbtFormat
import io.ejekta.kambrikx.serial.NbtSerializers
import kotlinx.serialization.*
import kotlinx.serialization.modules.overwriteWith
import kotlinx.serialization.modules.plus
import net.minecraft.nbt.NbtString
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box


fun <T> doJsonCycle(ser: KSerializer<T>, obj: T) {
    val json = Kambrik.Serial.Format

    val c = json.encodeToString(ser, obj)
    println("OBJ -> JSN: $c")
    val d = json.decodeFromString(ser, c)
    println("JSN: -> OBJ: $d")
}

@OptIn(KambrikExperimental::class)
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


@OptIn(KambrikExperimental::class)
fun main() {

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

    //*
    val text: Text = textLiteral("Hello!") {
        addLiteral("What's up?")
    }

    val format = Kambrik.Serial.Format

    val test = format.encodeToString( TextSerializer,
        text
    )

    println(test)

    val testBack = format.decodeFromString<Text>(test)

    println(testBack)

    //*/


}


