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

    fun <C : KambrikMsg> registerClientMessage(ser: KSerializer<C>, id: CustomPayload.Id<C>) {
        Kambridge.registerClientMessage(ser, id)
    }

    fun <S : KambrikMsg> registerServerMessage(ser: KSerializer<S>, id: CustomPayload.Id<S>) {
        Kambridge.registerServerMessage(ser, id)
    }

}