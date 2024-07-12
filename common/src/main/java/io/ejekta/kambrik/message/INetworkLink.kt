package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

interface INetworkLink<M : CustomPayload> {

    val id: CustomPayload.Id<M>
    val kClass: KClass<M>
    val ser: KSerializer<M>
    val json: Json

//    override fun getId(): CustomPayload.Id<out CustomPayload> {
//        return CustomPayload.id(id.toString())
//    }

    val packetCodec: PacketCodec<RegistryByteBuf, M>
        get() = PacketCodec.of(
            { value, buf -> buf.writeString(json.encodeToString(ser, value)) },
            { json.decodeFromString(ser, it.readString()) }
        )

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