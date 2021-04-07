@file:Suppress("EXPERIMENTAL_API_USAGE")
package io.ejekta.testing

import io.ejekta.kambrik.ext.wrapToPacketByteBuf
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.*
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos
import java.text.SimpleDateFormat
import java.util.*

val serMod = SerializersModule {

    polymorphic(Vehicle::class) {
        subclass(Car::class, Car.serializer())
    }

    polymorphic(Money::class) {
        subclass(Wallet::class, Wallet.serializer())
    }

    include(NbtFormat.BuiltInSerializers)
    //include(NbtFormat.ReferenceSerializers)

}

val NbtFormatTest = NbtFormat {
    serializersModule = serMod
    writePolymorphic = true
}

@Serializable
abstract class Vehicle(val typed: String)

@Serializable
data class Car(val wheels: Int) : Vehicle("Automobile") {
    override fun toString(): String {
        return "Car[wheels=$wheels, typed=$typed]"
    }
}

interface Money

@Serializable
data class Wallet(val amount: Double) : Money


@Serializable
data class Person(val name: String, val money: Money)



@Serializable
data class Holder(val tag: @Contextual Tag)

@Serializable
data class Nullie(val a: Int? = 1, val b: Int? = 10)

fun main(args: Array<String>) {

    val u = Nullie(a = 10, b = null)

    val config = NbtFormat {
        nullTag = StringTag.of("NULL")
    }

    val result = config.encodeToTag(Nullie.serializer(), u)
    val asString = result.toString()
    println(asString)

    println(
        StringNbtReader.parse(asString)
    )

    val result2 = config.decodeFromTag(Nullie.serializer(), result)

    println(result2)


}

/*
    @Serializable
    data class Holder(val tag: @Contextual Tag)

    val t = CompoundTag().apply {
        putString("Hai", "There")
        putByte("Yo", 1)
        putByte("Ma", 3)
        put("Blah", LongArrayTag(longArrayOf(5L, 10L, 15L)))
    }

    val u = Holder(t)

    //val result = NbtFormat.Default.encodeToTag(CompoundTagSerializer(), t)
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


