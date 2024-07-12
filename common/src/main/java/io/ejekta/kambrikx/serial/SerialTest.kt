package io.ejekta.kambrikx.serial

import codec
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import toKotlinJsonSerializer


@Serializable
data class JobStatus(val isWorking: Boolean)

@Serializable
data class MyPerson(val name: String, val age: Int, val neat: Boolean)

@Serializable
data class BonusDamage(val amount: Float)

@Serializable
data class Treasure(val location: @Contextual BlockPos)


fun main() {

    val json = Json {
        serializersModule = SerializersModule {
            codec(BlockPos.CODEC)
            codec(Vec3d.CODEC)
            codec(TextCodecs.CODEC)
        }
    }

    val item = Text.literal("Hello!")
    val ser = TextCodecs.CODEC.toKotlinJsonSerializer()

    val encoded = json.encodeToString(ser, item)

    println(encoded)

    val decoded = json.decodeFromString(ser, encoded)

    println(decoded)

}