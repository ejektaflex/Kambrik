package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.TestMsg
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import kotlinx.serialization.KSerializer
import net.minecraft.core.Registry
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import java.nio.file.Path

interface KambrikSharedApi {

    // Loader

    val platform: BridgePlatform

    // Messaging

    // * Client

    fun isOnClient(): Boolean

    fun isOnServer(): Boolean

    fun <M : KambrikMsg> registerClientMessage(serializer: KSerializer<M>, id: CustomPacketPayload.Type<M>): Boolean

    fun <M : KambrikMsg> sendMsgToClient(msg: M, player: ServerPlayer)

    // * Server

    fun <M : KambrikMsg> registerServerMessage(serializer: KSerializer<M>, id: CustomPacketPayload.Type<M>): Boolean

    fun <M : KambrikMsg> sendMsgToServer(msg: M)

    // Registration

    fun <T : Any> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T

    // Internal

    fun registerTestMessage() {
        if (!isOnServer()) {
            Kambrik.Message.registerClientMessage<TestMsg>(
                Identifier.fromNamespaceAndPath("kambrik", "test_msg")
            )
        }
    }

    fun getConfigDir(): Path

}
