package io.ejekta.kambrik.api.network

import io.ejekta.kambrik.ext.unwrapToTag
import io.ejekta.kambrik.ext.wrapToPacketByteBuf
import kotlinx.serialization.KSerializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

open class ClientNetworkLink<M : ClientMsg>(

    override val id: Identifier,
    override val ser: KSerializer<M>

    ) : INetworkLink<M>, ClientPlayNetworking.PlayChannelHandler {

    override fun register(): Boolean {
        return ClientPlayNetworking.registerGlobalReceiver(id, ::receive)
    }

    final override fun receive(
        client: MinecraftClient,
        handler: ClientPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        val contents = buf.unwrapToTag()
        val data = deserializePacket(contents)
        client.execute {
            data.onClientReceived(ClientMsg.MsgContext(client, handler, buf, responseSender))
        }
    }

    fun send(msg: M, players: Collection<ServerPlayerEntity>) {
        for (player in players) {
            ServerPlayNetworking.send(
                player,
                id,
                serializePacket(msg).wrapToPacketByteBuf()
            )
        }
    }

}