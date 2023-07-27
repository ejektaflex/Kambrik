package io.ejekta.kambrik.message

import io.ejekta.kambrik.bridge.Kambridge
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

class ServerNetworkLink<M : ServerMsg>(

    override val id: Identifier,
    override val kClass: KClass<M>,
    override val ser: KSerializer<M>,
    override val json: Json = INetworkLink.defaultJson

    ) : INetworkLink<M> {

    override fun register(): Boolean {
        return Kambridge.registerServerMessage(this)
    }

    fun send(msg: M) {
        Kambridge.sendMsgToServer(this, msg, id)
    }

}