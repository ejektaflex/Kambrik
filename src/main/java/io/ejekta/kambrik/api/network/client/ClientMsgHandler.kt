package io.ejekta.kambrik.api.network.client

import io.ejekta.kambrik.ext.unwrapToTag
import io.ejekta.kambrik.api.network.IKambrikMsgHandler
import io.ejekta.kambrik.api.network.IPacketInfo
import io.ejekta.kambrik.api.network.PacketInfo
import kotlinx.serialization.KSerializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

open class ClientMsgHandler<M : ClientMsg<M>>(private val info: IPacketInfo<M>) : ClientPlayNetworking.PlayChannelHandler,
    IKambrikMsgHandler, IPacketInfo<M> by info {

    constructor(idPair: Pair<Identifier, () -> KSerializer<M>>) : this(PacketInfo(idPair.first, idPair.second))

    override fun register() {
        ClientPlayNetworking.registerGlobalReceiver(info.id, ::receive)
    }

    override fun receive(
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

    companion object {
        fun <T : ClientMsg<T>> dummy(): ClientMsgHandler<T> {
            return ClientMsgHandler(IPacketInfo.dummy())
        }
    }

}