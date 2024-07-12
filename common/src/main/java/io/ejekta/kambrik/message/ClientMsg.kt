package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.Serializable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity

/**
 * This represents a serializable message that can be sent to a client.
 */
@Serializable
abstract class ClientMsg : CustomPayload {

    open fun onClientReceived() {
        // Executes on client thread
    }

    fun sendToClient(player: ServerPlayerEntity) {
        Kambrik.Message.sendClientMsg(this, listOf(player))
    }

    fun sendToClients(players: Collection<ServerPlayerEntity>) {
        Kambrik.Message.sendClientMsg(this, players)
    }

}