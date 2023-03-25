package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.input.KambrikKeybind
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.option.KeyBinding
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class KambrikSharedApiFabric : KambrikSharedApi {

    override val side: BridgeSide
        get() = BridgeSide.FABRIC

    // Messaging

    override fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>, id: Identifier): Boolean {
        return ClientPlayNetworking.registerGlobalReceiver(id) { client, handler, buf, responseSender ->
            val contents = buf.readString()
            val data = link.deserializePacket(contents)
            client.execute {
                data.onClientReceived(ClientMsg.MsgContext(client))
            }
        }
    }

    override fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity, msgId: Identifier) {
        ServerPlayNetworking.send(
            player,
            msgId,
            PacketByteBufs.create().apply {
                writeString(link.serializePacket(msg))
            }
        )
    }

    override fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>, id: Identifier): Boolean {
        return ServerPlayNetworking.registerGlobalReceiver(id) { server, player, handler, buf, responseSender ->
            val contents = buf.readString()
            val data = link.deserializePacket(contents)
            server.execute {
                data.onServerReceived(ServerMsg.MsgContext(player))
            }
        }
    }

    override fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M, msgId: Identifier) {
        ClientPlayNetworking.send(
            msgId,
            PacketByteBufs.create().apply {
                writeString(link.serializePacket(msg))
            }
        )
    }

    // Registration

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        return KambrikRegistrar.register(autoReg, reg, thingId, obj)
    }

}