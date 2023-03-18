package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

class KambrikMessageApi internal constructor() {

    @PublishedApi
    internal val clientLinks = mutableMapOf<KClass<*>, ClientNetworkLink<*>>()

    @PublishedApi
    internal val serverLinks = mutableMapOf<KClass<*>, ServerNetworkLink<*>>()

    var serializersModule: SerializersModule = Kambrik.Serial.DefaultSerializers
        private set

    @PublishedApi
    internal inline fun <reified M : Any> registerMessage(
        linkMaker: () -> INetworkLink<M>,
        reg: MutableMap<KClass<*>, INetworkLink<M>>
    ) : INetworkLink<M> {
        val linkage = linkMaker()
        val result = linkage.register()

        if (!result) {
            throw Exception("Cannot register ${linkage.id}! This global channel already exists.")
        }

        reg[M::class] = linkage
        return linkage
    }

    fun addSerializerModule(newSerializersModule: SerializersModule) {
        serializersModule = SerializersModule {
            include(serializersModule)
            include(newSerializersModule)
        }
    }


    inline fun <reified C : ClientMsg> registerClientMessage(ser: KSerializer<C>, id: Identifier): INetworkLink<C> {
        return registerMessage({ ClientNetworkLink(id, ser) }, clientLinks as MutableMap<KClass<*>, INetworkLink<C>>)
    }

    inline fun <reified S : ServerMsg> registerServerMessage(ser: KSerializer<S>, id: Identifier): INetworkLink<S> {
        return registerMessage({ ServerNetworkLink(id, ser) }, serverLinks as MutableMap<KClass<*>, INetworkLink<S>>)
    }

    internal fun <C : ClientMsg> sendClientMsg(msg: C, players: Collection<ServerPlayerEntity>) {
        val link = clientLinks[msg::class] as? ClientNetworkLink<C> ?: throw Exception("Unable to send message! Has it been registered?")
        link.send(msg, players)
    }

    internal fun <S : ServerMsg> sendServerMsg(msg: S) {
        val link = serverLinks[msg::class] as? ServerNetworkLink<S> ?: throw Exception("Unable to send message! Has it been registered?")
        link.send(msg)
    }

}