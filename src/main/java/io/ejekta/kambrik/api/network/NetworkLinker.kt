package io.ejekta.kambrik.api.network

import io.ejekta.kambrik.api.network.client.ClientMsg
import io.ejekta.kambrik.api.network.client.ClientNetworkLink
import kotlinx.serialization.KSerializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

object NetworkLinker {

    val typeLinks = mutableMapOf<KClass<*>, ClientNetworkLink<*>>()

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified S : ClientMsg<S>> linkClientMsg(ser: KSerializer<S>, id: Identifier): ClientNetworkLink<S> {
        val linkage = ClientNetworkLink(id, ser)
        linkage.register()

        typeLinks[S::class] = linkage

        return linkage
    }

    inline fun <reified M : ClientMsg<M>> sendClientMsg(msg: M, player: ServerPlayerEntity) {

        val link = typeLinks[msg::class]!! as ClientNetworkLink<M>

        println("Link: $link")

        link.send(msg, player)
    }

}