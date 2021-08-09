package io.ejekta.kambrik.api.network

import io.ejekta.kambrik.api.network.client.ClientMsg
import io.ejekta.kambrik.api.network.client.ClientNetworkLink
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

object KambrikMessages {

    val typeLinks = mutableMapOf<KClass<*>, ClientNetworkLink<*>>()

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified S : ClientMsg<S>> registerClientMessage(ser: KSerializer<S>, id: Identifier): ClientNetworkLink<S> {
        val linkage = ClientNetworkLink(id, ser)

        val result = linkage.register()

        if (!result) {
            throw Exception("Cannot register $id! This global channel already exists.")
        }

        typeLinks[S::class] = linkage
        return linkage
    }

    fun <M : ClientMsg<M>> sendClientMsg(msg: M, player: ServerPlayerEntity) {
        val link = typeLinks[msg::class]!! as ClientNetworkLink<M>
        println("Link: $link")
        link.send(msg, player)
    }

}