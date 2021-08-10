package io.ejekta.kambrik.api.message

import io.ejekta.kambrik.ext.unwrapToTag
import io.ejekta.kambrik.ext.wrapToPacketByteBuf
import kotlinx.serialization.KSerializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class ServerNetworkLink<M : ServerMsg>(

    override val id: Identifier,
    override val ser: KSerializer<M>

    ) : INetworkLink<M>, ServerPlayNetworking.PlayChannelHandler {

    override fun register(): Boolean {
        return ServerPlayNetworking.registerGlobalReceiver(id, ::receive)
    }

    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        val contents = buf.unwrapToTag()
        val data = deserializePacket(contents)
        server.execute {
            data.onServerReceived(ServerMsg.MsgContext(server, player, handler, buf, responseSender))
        }
    }

    fun send(msg: M) {
        ClientPlayNetworking.send(
            id,
            serializePacket(msg).wrapToPacketByteBuf()
        )
    }

}