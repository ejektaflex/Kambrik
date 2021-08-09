package io.ejekta.kambrik.api.network.client

import io.ejekta.kambrik.ext.unwrapToTag
import io.ejekta.kambrik.api.network.IKambrikMsgHandler
import io.ejekta.kambrik.ext.wrapToPacketByteBuf
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.KSerializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

open class ClientNetworkLink<M : ClientMsg<M>>(

    private val id: Identifier,
    private val ser: KSerializer<M>

    ) : ClientPlayNetworking.PlayChannelHandler,
    IKambrikMsgHandler {

    override fun register(): Boolean {
        return ClientPlayNetworking.registerGlobalReceiver(id, ::receive)
    }

    private fun serializePacket(m: M): NbtElement {
        return NbtFormat.Default.encodeToTag(ser, m)
    }

    private fun deserializePacket(tag: NbtElement): M {
        return NbtFormat.Default.decodeFromTag(ser, tag)
    }

    final override fun receive(
        client: MinecraftClient,
        handler: ClientPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        val contents = buf.unwrapToTag()
        val data = deserializePacket(contents)
        client.execute {
            data.onClientReceived(ClientMsg.ClientMsgContext(client, handler, buf, responseSender))
        }
    }

    fun send(msg: M, player: ServerPlayerEntity) {
        ServerPlayNetworking.send(
            player,
            id,
            serializePacket(msg).wrapToPacketByteBuf()
        )
    }

}