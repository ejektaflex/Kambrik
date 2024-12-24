package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.internal.TestMsg
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

/**
 * This represents a serializable message that can be sent to a client.
 */
@Serializable
abstract class KambrikMsg : CustomPacketPayload {

    data class MsgContext(
        val player: ServerPlayer
    )

    open fun onClientReceived() {
        // Executes on client thread
    }

    open fun onServerReceived(ctx: MsgContext) {
        // Executes on server thread
    }

    fun sendToClient(player: ServerPlayer) {
        Kambridge.sendMsgToClient(this, player)
    }

    fun sendToClients(players: Collection<ServerPlayer>) {
        for (player in players) {
            Kambridge.sendMsgToClient(this, player)
        }
    }

    fun sendToServer() {
        Kambridge.sendMsgToServer(this)
    }

    override fun type(): CustomPacketPayload.Type<out TestMsg> {
        return Kambrik.Message.payloadMap[this::class] as CustomPacketPayload.Type<out TestMsg>
    }
}