package io.ejekta.kambrik.api.network.server

import io.ejekta.kambrik.ext.wrapToPacketByteBuf
import io.ejekta.kambrik.api.network.KambrikMessage
import io.ejekta.kambrik.api.network.IPacketInfo
import io.ejekta.kambrik.api.network.IPacketInfo.Companion.dummy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
open class ServerMsg<M : ServerMsg<M>>(@Transient val info: IPacketInfo<M> = dummy()) : KambrikMessage, IPacketInfo<M> by info {

    data class ServerMsgContext(
        val server: MinecraftServer,
        val player: ServerPlayerEntity,
        val handler: ServerPlayNetworkHandler,
        val responseSender: PacketSender
    )

    open fun onServerReceived(ctx: ServerMsgContext) {

    }

    fun sendToServer() {
        ClientPlayNetworking.send(
            id,
            serializePacket(this as M).wrapToPacketByteBuf()
        )
    }
}