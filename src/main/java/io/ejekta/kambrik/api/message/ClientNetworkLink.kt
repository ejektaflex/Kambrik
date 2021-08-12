package io.ejekta.kambrik.api.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class ClientNetworkLink<M : ClientMsg>(

    override val id: Identifier,
    override val ser: KSerializer<M>,
    override val json: Json = INetworkLink.defaultJson

    ) : INetworkLink<M> {

    override fun register(): Boolean {
        if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
            return true // fake it w/o registering on Server
        }
        return ClientPlayNetworking.registerGlobalReceiver(id) { client, handler, buf, responseSender ->
            val contents = buf.readString()
            val data = deserializePacket(contents)
            client.execute {
                data.onClientReceived(ClientMsg.MsgContext(client, handler, buf, responseSender))
            }
        }
    }

    fun send(msg: M, players: Collection<ServerPlayerEntity>) {
        for (player in players) {
            ServerPlayNetworking.send(
                player,
                id,
                PacketByteBufs.create().apply {
                    writeString(serializePacket(msg))
                }
            )
        }
    }

}