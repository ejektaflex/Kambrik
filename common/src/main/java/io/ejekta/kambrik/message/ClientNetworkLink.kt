package io.ejekta.kambrik.message

import io.ejekta.kambrik.bridge.Kambridge
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.reflect.KClass

class ClientNetworkLink<M : KambrikMsg>(

    override val id: CustomPayload.Id<M>,
    override val ser: KSerializer<M>,
    override val json: Json = INetworkLink.defaultJson

    ) : INetworkLink<M> {

    override fun register(): Boolean {
        return Kambridge.registerClientMessage(this)
    }

    fun send(msg: M, players: Collection<ServerPlayerEntity>) {
        for (player in players) {
            Kambridge.sendMsgToClient(this, msg, player)
        }
    }

}