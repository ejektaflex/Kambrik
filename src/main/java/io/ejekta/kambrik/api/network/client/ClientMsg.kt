package io.ejekta.kambrik.api.network.client

import io.ejekta.kambrik.api.network.KambrikMessage
import io.ejekta.kambrik.api.network.NetworkLinker
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
abstract class ClientMsg<M : ClientMsg<M>>() : KambrikMessage {

    data class ClientMsgContext(
        val client: MinecraftClient,
        val handler: ClientPlayNetworkHandler,
        val buf: PacketByteBuf,
        val responseSender: PacketSender
    )

    open fun onClientReceived(ctx: ClientMsgContext) {
        // Executes on client thread
    }

    fun sendToClient(player: ServerPlayerEntity) {
        NetworkLinker.sendClientMsg(this as M, player)
    }

}