package io.ejekta.kambrik.internal

import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

@Serializable
data class TestMsg(val msg: String, @Contextual val id: ResourceLocation) : KambrikMsg() {
    override fun onClientReceived() {
        println("Got Test Msg! It says: $msg")
    }

    override fun type(): CustomPacketPayload.Type<TestMsg> = ID

    companion object {
        val ID: CustomPacketPayload.Type<TestMsg> = CustomPacketPayload.Type(
            ResourceLocation.fromNamespaceAndPath("kambrik", "test_msg")
        )
    }
}