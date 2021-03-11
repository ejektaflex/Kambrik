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
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.LongTag
import net.minecraft.util.math.BlockPos

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


fun main(args: Array<String>) {

    @Serializable
    data class SlingshotData(val name: String,  val pos: @Contextual BlockPos)

    val data = SlingshotData("Neat Slingshot", BlockPos(1, 2, 3))

    val asNbt = NbtFormatTest.encodeToTag(data)
    println("As Nbt: $asNbt")

    val asObj = NbtFormatTest.decodeFromTag<SlingshotData>(asNbt)
    println("As Obj: $asObj")

}


