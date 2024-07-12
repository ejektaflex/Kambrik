package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.Kambridge
import kotlinx.serialization.Serializable
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity

/**
 * This represents a serializable message that can be sent to a client.
 */
@Serializable
abstract class KambrikMsg : CustomPayload {

    data class MsgContext(
        val player: ServerPlayerEntity
    )

    open fun onClientReceived() {
        // Executes on client thread
    }

    open fun onServerReceived(ctx: MsgContext) {
        // Executes on server thread
    }

    fun sendToClient(player: ServerPlayerEntity) {
        Kambridge.sendMsgToClient(this, player)
    }

    fun sendToClients(players: Collection<ServerPlayerEntity>) {
        for (player in players) {
            Kambridge.sendMsgToClient(this, player)
        }
    }

    fun sendToServer() {
        Kambridge.sendMsgToServer(this)
    }

}