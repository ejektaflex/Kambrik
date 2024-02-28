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

    override fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>): Boolean {
        return ClientPlayNetworking.registerGlobalReceiver(link.id) { client, handler, buf, responseSender ->
            val contents = buf.readString()
            val data = link.deserializePacket(contents)
            client.execute {
                data.onClientReceived()
            }
        }
    }

    override fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity) {
        ServerPlayNetworking.send(
            player,
            link.id,
            PacketByteBufs.create().apply {
                writeString(link.serializePacket(msg))
            }
        )
    }

    override fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>): Boolean {
        return ServerPlayNetworking.registerGlobalReceiver(link.id) { server, player, handler, buf, responseSender ->
            val contents = buf.readString()
            val data = link.deserializePacket(contents)
            server.execute {
                data.onServerReceived(ServerMsg.MsgContext(player))
            }
        }
    }

    override fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M) {
        ClientPlayNetworking.send(
            link.id,
            PacketByteBufs.create().apply {
                writeString(link.serializePacket(msg))
            }
        )
    }

    // Registration

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        return KambrikRegistrar.register(autoReg, reg, thingId, lazyOf(obj)).value
    }

}