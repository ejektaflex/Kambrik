package io.ejekta.kambrik.api.message

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier

interface INetworkLink<M> {

    val id: Identifier
    val ser: KSerializer<M>
    val json: Json

    fun register(): Boolean

    fun serializePacket(m: M): String {
        return json.encodeToString(ser, m)
    }

    fun deserializePacket(str: String): M {
        return json.decodeFromString(ser, str)
    }

    companion object {
        val defaultJson = Json {
            serializersModule = NbtFormat.Default.serializersModule
        }
    }

}