package io.ejekta.kambrik.api.network.server

import io.ejekta.kambrik.ext.unwrapToTag
import io.ejekta.kambrik.api.network.IKambrikMsgHandler
import io.ejekta.kambrik.api.network.IPacketInfo
import io.ejekta.kambrik.api.network.PacketInfo
import kotlinx.serialization.KSerializer
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

open class ServerMsgHandler<M : ServerMsg<M>>(private val info: IPacketInfo<M>) : ServerPlayNetworking.PlayChannelHandler,
    IKambrikMsgHandler, IPacketInfo<M> by info {

    override fun register() {
        ServerPlayNetworking.registerGlobalReceiver(info.id, ::receive)
    }

    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        val contents = buf.unwrapToTag()
        val data = info.deserializePacket(contents)
        server.execute {
            data.onServerReceived(ServerMsg.ServerMsgContext(server, player, handler, responseSender))
        }
    }
}