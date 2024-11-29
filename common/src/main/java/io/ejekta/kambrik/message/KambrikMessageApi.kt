package io.ejekta.kambrik.message

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.Kambridge
import kotlinx.serialization.KSerializer
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class KambrikMessageApi internal constructor() {

    init {
        Kambrik.Logger.debug("Kambrik Message API Initialized.")
    }

    fun <C : KambrikMsg> registerClientMessage(ser: KSerializer<C>, id: CustomPacketPayload.Type<C>) {
        Kambridge.registerClientMessage(ser, id)
    }

    fun <S : KambrikMsg> registerServerMessage(ser: KSerializer<S>, id: CustomPacketPayload.Type<S>) {
        Kambridge.registerServerMessage(ser, id)
    }

}