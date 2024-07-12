package io.ejekta.kambrik.fabric.bridge

import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity

class KambrikSharedApiFabric : KambrikSharedApi {

    override val side: BridgeSide
        get() = BridgeSide.FABRIC

    // Messaging

    override fun isOnClient(): Boolean {
        return FabricLoader.getInstance().environmentType == EnvType.CLIENT
    }

    override fun isOnServer(): Boolean {
        return FabricLoader.getInstance().environmentType == EnvType.SERVER
    }

    private fun <M :  KambrikMsg> createPacketCodec(id: CustomPayload.Id<M>, serializer: KSerializer<M>): PacketCodec<RegistryByteBuf, M> {
        val json = INetworkLink.defaultJson
        return PacketCodec.of(
            { value, buf -> buf.writeString(json.encodeToString(serializer, value)) },
            { json.decodeFromString(serializer, it.readString()) }
        )
    }

    override fun <M : KambrikMsg> registerClientMessage(serializer: KSerializer<M>, id: CustomPayload.Id<M>): Boolean {
        PayloadTypeRegistry.playS2C().register(id,createPacketCodec(id, serializer))
        return ClientPlayNetworking.registerGlobalReceiver(id) { payload, context ->
            (payload as KambrikMsg).onClientReceived()
        }
    }

    override fun <M : KambrikMsg> registerServerMessage(serializer: KSerializer<M>, id: CustomPayload.Id<M>): Boolean {
        PayloadTypeRegistry.playS2C().register(id, createPacketCodec(id, serializer))
        return ClientPlayNetworking.registerGlobalReceiver(id) { payload, context ->
            (payload as KambrikMsg).onClientReceived()
        }
    }

    override fun <M : KambrikMsg> sendMsgToClient(msg: M, player: ServerPlayerEntity) {
        ServerPlayNetworking.send(player, msg)
    }

    override fun <M : KambrikMsg> sendMsgToServer(msg: M) {
        ClientPlayNetworking.send(msg)
    }

    // Registration

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        return KambrikRegistrar.register(autoReg, reg, thingId, lazyOf(obj)).value
    }

}