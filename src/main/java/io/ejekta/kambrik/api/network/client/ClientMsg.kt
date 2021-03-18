package io.ejekta.kambrik.api.network.client

import io.ejekta.kambrik.ext.wrapToPacketByteBuf
import io.ejekta.kambrik.api.network.KambrikMessage
import io.ejekta.kambrik.api.network.IPacketInfo
import io.ejekta.kambrik.api.network.IPacketInfo.Companion.dummy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
open class ClientMsg<M : ClientMsg<M>>(@Transient val info: IPacketInfo<M> = dummy()) : KambrikMessage, IPacketInfo<M> by info {

    data class ClientMsgContext(
        val client: MinecraftClient,
        val handler: ClientPlayNetworkHandler,
        val buf: PacketByteBuf,
        val responseSender: PacketSender
    )

    open fun onClientReceived(ctx: ClientMsgContext) {

    }

    fun sendToClient(player: ServerPlayerEntity) {
        ServerPlayNetworking.send(
            player,
            id,
            serializePacket(this as M).wrapToPacketByteBuf()
        )
    }
}