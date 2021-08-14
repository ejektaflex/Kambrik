package io.ejekta.kambrik.api.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

@Serializable
abstract class ServerMsg() {

    data class MsgContext(
        val server: MinecraftServer,
        val player: ServerPlayerEntity,
        val handler: ServerPlayNetworkHandler,
        val buf: PacketByteBuf,
        val responseSender: PacketSender
    )

    open fun onServerReceived(ctx: MsgContext) {
        // Executes on client thread
    }

    fun sendToServer() {
        Kambrik.Message.sendServerMsg(this)
    }

}