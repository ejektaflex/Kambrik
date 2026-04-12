package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.Kambridge
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import kotlin.reflect.KClass

class KambrikMessageApi internal constructor() {

    init {
        Kambrik.Logger.debug("Kambrik Message API Initialized.")
    }

    @PublishedApi
    internal val payloadMap = mutableMapOf<KClass<out KambrikMsg>, CustomPacketPayload.Type<*>>()

    inline fun <reified C : KambrikMsg> registerClientMessage(id: Identifier, ser: KSerializer<C> = serializer<C>()) {
        val clazz = C::class
        val payloadId = CustomPacketPayload.Type<C>(id)
        payloadMap[clazz] = payloadId
        Kambridge.registerClientMessage(ser, payloadId)
    }

    inline fun <reified S : KambrikMsg> registerServerMessage(id: Identifier, ser: KSerializer<S> = serializer<S>()) {
        val clazz = S::class
        val payloadId = CustomPacketPayload.Type<S>(id)
        payloadMap[clazz] = payloadId
        Kambridge.registerServerMessage(ser, payloadId)
    }

}