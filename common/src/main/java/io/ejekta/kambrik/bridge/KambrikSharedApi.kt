package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.TestMsg
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import kotlinx.serialization.KSerializer
import net.minecraft.network.packet.CustomPayload
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity

interface KambrikSharedApi {

    // Loader

    val side: BridgeSide

    // Messaging

    // * Client

    fun isOnClient(): Boolean

    fun isOnServer(): Boolean

    fun <M : KambrikMsg> registerClientMessage(serializer: KSerializer<M>, id: CustomPayload.Id<M>): Boolean

    fun <M : KambrikMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity)

    // * Server

    fun <M : KambrikMsg> registerServerMessage(serializer: KSerializer<M>, id: CustomPayload.Id<M>): Boolean

    fun <M : KambrikMsg> sendMsgToServer(link: INetworkLink<M>, msg: M)

    // Registration

    fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T

    // Internal

    fun registerTestMessage() {
        Kambrik.Message.registerClientMessage(
            TestMsg.serializer(),
            TestMsg.ID
        )
    }

}