package io.ejekta.kambrik.api.network

import kotlinx.serialization.KSerializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

object KambrikMessages {

    val clientLinks = mutableMapOf<KClass<*>, ClientNetworkLink<*>>()

    inline fun <reified S : ClientMsg> registerClientMessage(ser: KSerializer<S>, id: Identifier): ClientNetworkLink<S> {
        val linkage = ClientNetworkLink(id, ser)

        val result = linkage.register()

        if (!result) {
            throw Exception("Cannot register $id! This global channel already exists.")
        }

        clientLinks[S::class] = linkage
        return linkage
    }

    internal fun <M : ClientMsg> sendClientMsg(msg: M, players: Collection<ServerPlayerEntity>) {
        val link = clientLinks[msg::class] as? ClientNetworkLink<M> ?: throw Exception("Unable to send message! Has it been registered?")
        link.send(msg, players)
    }

}