package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
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
            serializersModule = Kambrik.Serial.DefaultSerializers
        }
    }

}