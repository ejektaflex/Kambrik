package io.ejekta.kambrik.fabric.bridge

import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
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

    override fun <M : KambrikMsg> registerClientMessage(link: INetworkLink<M>): Boolean {

        PayloadTypeRegistry.playS2C().register(link.id, link.packetCodec)

        return ClientPlayNetworking.registerGlobalReceiver(link.id) { payload, context ->
            (payload as KambrikMsg).onClientReceived()
        }
    }

    override fun <M : KambrikMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity) {
        ServerPlayNetworking.send(player, msg)
    }

    override fun <M : KambrikMsg> registerServerMessage(link: INetworkLink<M>): Boolean {

        PayloadTypeRegistry.playC2S().register(link.id, link.packetCodec)

        return ServerPlayNetworking.registerGlobalReceiver(
            link.id
        ) { payload, context ->
            payload.onServerReceived(
                KambrikMsg.MsgContext(context.player())
            )
        }
    }

    override fun <M : KambrikMsg> sendMsgToServer(link: INetworkLink<M>, msg: M) {
        ClientPlayNetworking.send(msg)
    }

    // Registration

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        return KambrikRegistrar.register(autoReg, reg, thingId, lazyOf(obj)).value
    }

}