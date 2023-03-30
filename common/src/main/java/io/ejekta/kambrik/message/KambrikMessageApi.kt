package io.ejekta.kambrik.message

import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.Kambridge
import kotlinx.serialization.KSerializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

class KambrikMessageApi internal constructor() {

    @PublishedApi
    internal val clientLinks = mutableMapOf<KClass<*>, ClientNetworkLink<*>>()

    @PublishedApi
    internal val serverLinks = mutableMapOf<KClass<*>, ServerNetworkLink<*>>()

    @PublishedApi
    internal fun <M : Any> registerMessage(
        linkMaker: () -> INetworkLink<M>,
        reg: MutableMap<KClass<*>, INetworkLink<M>>,
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

        reg[linkage.kClass] = linkage
        return linkage
    }

    fun <C : ClientMsg> registerClientMessage(ser: KSerializer<C>, klass: KClass<C>, id: Identifier): INetworkLink<C> {
        val shouldClientLinkRegister = if (Kambridge.side == BridgeSide.FORGE) {
            true
        } else {
            Kambridge.isOnClient()
        }
        return registerMessage({ ClientNetworkLink(id, klass, ser) }, clientLinks as MutableMap<KClass<*>, INetworkLink<C>>, shouldClientLinkRegister)
    }

    fun <S : ServerMsg> registerServerMessage(ser: KSerializer<S>, klass: KClass<S>, id: Identifier): INetworkLink<S> {
        return registerMessage({ ServerNetworkLink(id, klass, ser) }, serverLinks as MutableMap<KClass<*>, INetworkLink<S>>, true)
    }

    internal fun <C : ClientMsg> sendClientMsg(msg: C, players: Collection<ServerPlayerEntity>) {
        val link = clientLinks[msg::class] as? ClientNetworkLink<C> ?: throw Exception("Unable to send message! Has it been registered?").also {
            it.printStackTrace()
        }
        link.send(msg, players)
    }

    internal fun <S : ServerMsg> sendServerMsg(msg: S) {
        val link = serverLinks[msg::class] as? ServerNetworkLink<S> ?: throw Exception("Unable to send message! Has it been registered?").also {
            it.printStackTrace()
        }
        link.send(msg)
    }

}