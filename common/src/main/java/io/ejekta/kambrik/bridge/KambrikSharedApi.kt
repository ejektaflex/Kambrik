package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.TestMsg
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

interface KambrikSharedApi {

    // Loader

    val side: BridgeSide

    // Messaging

    // * Client

    fun isOnClient(): Boolean

    fun isOnServer(): Boolean

    fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>): Boolean

    fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity)

    // * Server

    fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>): Boolean

    fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M)

    // Registration

    fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T

    // Internal

    fun registerTestMessage() {
        Kambrik.Message.registerClientMessage(
            TestMsg.serializer(),
            TestMsg::class,
            Kambrik.idOf("test_msg")
        )
    }

}