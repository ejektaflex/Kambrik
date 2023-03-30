package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

interface INetworkLink<M : Any> {

    val id: Identifier
    val kClass: KClass<M>
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