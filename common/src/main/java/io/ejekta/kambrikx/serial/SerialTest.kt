package io.ejekta.kambrikx.serial

import io.ejekta.kambrikx.percale.deserialize
import io.ejekta.kambrikx.percale.serialize
import kotlinx.serialization.Serializable
import net.minecraft.component.DataComponentTypes
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps

@Serializable
data class JobStatus(val isWorking: Boolean)

@Serializable
data class MyPerson(val name: String, val age: Int, val jobStatus: JobStatus)

@Serializable
data class BonusDamage(val amount: Float)

fun main() {
    val data = MyPerson("John", 35, JobStatus(false))

    // Encoding a Kotlin object to Nbt
    val encodedData = NbtOps.INSTANCE.serialize(data)
    println(encodedData) //=> {age:35,jobStatus:{isWorking:0b},name:"John"}

    // Decoding NbtElements back to MyDataClass
    val decodedData = NbtOps.INSTANCE.deserialize<NbtElement, MyPerson>(encodedData!!)
    println(decodedData) //=> MyPerson(name=John, age=35, jobStatus=JobStatus(isWorking=false))
}