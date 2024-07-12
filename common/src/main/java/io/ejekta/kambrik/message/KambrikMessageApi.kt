package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.Kambridge
import kotlinx.serialization.KSerializer
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.reflect.KClass

class KambrikMessageApi internal constructor() {

    init {
        Kambrik.Logger.debug("Kambrik Message API Initialized.")
    }

    @PublishedApi
    internal val clientLinks = mutableMapOf<CustomPayload.Id<*>, ClientNetworkLink<*>>()

    @PublishedApi
    internal val serverLinks = mutableMapOf<CustomPayload.Id<*>, ServerNetworkLink<*>>()

    @PublishedApi
    internal fun <M : CustomPayload> registerMessage(
        linkMaker: () -> INetworkLink<M>,
        reg: MutableMap<CustomPayload.Id<M>, INetworkLink<M>>,
        shouldRegLink: Boolean
    ) : INetworkLink<M> {
        val linkage = linkMaker()

        val result = if (shouldRegLink) {
            linkage.register()
        } else {
            true
        }

        if (!result) {
            throw Exception("Cannot register ${linkage.id}! This global channel already exists.")
        }

        reg[linkage.id] = linkage
        return linkage
    }

    fun <C : KambrikMsg> registerClientMessage(ser: KSerializer<C>, id: CustomPayload.Id<C>): INetworkLink<C> {
        val shouldClientLinkRegister = if (Kambridge.side == BridgeSide.FORGE) {
            true
        } else {
            Kambridge.isOnClient()
        }
        return registerMessage({ ClientNetworkLink(id, ser) }, clientLinks as MutableMap<CustomPayload.Id<C>, INetworkLink<C>>, shouldClientLinkRegister)
    }

    fun <S : KambrikMsg> registerServerMessage(ser: KSerializer<S>, id: CustomPayload.Id<S>): INetworkLink<S> {
        return registerMessage({ ServerNetworkLink(id, ser) }, serverLinks as MutableMap<CustomPayload.Id<S>, INetworkLink<S>>, true)
    }

    internal fun <C : KambrikMsg> sendClientMsg(msg: C, players: Collection<ServerPlayerEntity>) {
        val link = clientLinks[msg.id] as? ClientNetworkLink<C> ?: throw Exception("Unable to send message! Has it been registered?").also {
            Kambrik.Logger.debug("Client Links: ${clientLinks.map { link -> link.value.id }}")
            it.printStackTrace()
        }
        link.send(msg, players)
    }

    internal fun <S : KambrikMsg> sendServerMsg(msg: S) {
        val link = serverLinks[msg.id] as? ServerNetworkLink<S> ?: throw Exception("Unable to send message! Has it been registered?").also {
            Kambrik.Logger.debug("Server Links: ${serverLinks.map { link -> link.value.id }}")
            it.printStackTrace()
        }
        link.send(msg)
    }

}