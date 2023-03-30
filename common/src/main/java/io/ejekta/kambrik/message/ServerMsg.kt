package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.Serializable
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
abstract class ServerMsg {

    data class MsgContext(
        val player: ServerPlayerEntity
    )

    open fun onServerReceived(ctx: MsgContext) {
        // Executes on client thread
    }

    fun sendToServer() {
        Kambrik.Message.sendServerMsg(this)
    }

}