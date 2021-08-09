package io.ejekta.kambrik.api.network

import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
abstract class ClientMsg() {

    data class MsgContext(
        val client: MinecraftClient,
        val handler: ClientPlayNetworkHandler,
        val buf: PacketByteBuf,
        val responseSender: PacketSender
    )

    open fun onClientReceived(ctx: MsgContext) {
        // Executes on client thread
    }

    fun sendToClient(player: ServerPlayerEntity) {
        KambrikMessages.sendClientMsg(this, listOf(player))
    }

    fun sendToClients(players: Collection<ServerPlayerEntity>) {
        KambrikMessages.sendClientMsg(this, players)
    }

}