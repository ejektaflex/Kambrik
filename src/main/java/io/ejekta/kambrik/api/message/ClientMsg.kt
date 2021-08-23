package io.ejekta.kambrik.api.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.Serializable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

/**
 * This represents a serializable message that can be sent to a client.
 */
@Serializable
abstract class ClientMsg {

    @Environment(EnvType.CLIENT)
    data class MsgContext(
        val client: MinecraftClient,
        val handler: ClientPlayNetworkHandler,
        val buf: PacketByteBuf,
        val responseSender: PacketSender
    )

    @Environment(EnvType.CLIENT)
    open fun onClientReceived(ctx: MsgContext) {
        // Executes on client thread
    }

    fun sendToClient(player: ServerPlayerEntity) {
        Kambrik.Message.sendClientMsg(this, listOf(player))
    }

    fun sendToClients(players: Collection<ServerPlayerEntity>) {
        Kambrik.Message.sendClientMsg(this, players)
    }

}