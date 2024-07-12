package io.ejekta.kambrik.fabric.bridge

import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
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

    override fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>): Boolean {

        PayloadTypeRegistry.playS2C().register(link.id, link.packetCodec)

        return ClientPlayNetworking.registerGlobalReceiver(link.id) { payload, context ->
            (payload as ClientMsg).onClientReceived()
        }
    }

    override fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity) {
        ServerPlayNetworking.send(player, msg)
    }

    override fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>): Boolean {

        PayloadTypeRegistry.playC2S().register(link.id, link.packetCodec)

        return ServerPlayNetworking.registerGlobalReceiver(
            link.id
        ) { payload, context ->
            payload.onServerReceived(
                ServerMsg.MsgContext(context.player())
            )
        }
    }

    override fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M) {
        // TODO("Networking")
        ClientPlayNetworking.send(msg)
//        ClientPlayNetworking.send(
//            link.id,
//            PacketByteBufs.create().apply {
//                writeString(link.serializePacket(msg))
//            }
//        )
    }

    // Registration

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        return KambrikRegistrar.register(autoReg, reg, thingId, lazyOf(obj)).value
    }

}