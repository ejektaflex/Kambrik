package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import kotlin.reflect.KClass

interface INetworkLink<M : CustomPacketPayload> {

    val id: CustomPacketPayload.Type<M>
    val ser: KSerializer<M>
    val json: Json

//    override fun getId(): CustomPacketPayload.Type<out CustomPayload> {
//        return CustomPayload.id(id.toString())
//    }

    val packetCodec: StreamCodec<RegistryFriendlyByteBuf, M>
        get() = StreamCodec.of(
            { buf, value ->
                buf.writeUtf(json.encodeToString(ser, value))
            },
            { json.decodeFromString(ser, it.readUtf()) }
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