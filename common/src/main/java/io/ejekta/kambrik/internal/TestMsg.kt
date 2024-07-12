package io.ejekta.kambrik.internal

import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable
import net.minecraft.network.packet.CustomPayload

@Serializable
data class TestMsg(val msg: String) : ClientMsg() {
    override fun onClientReceived() {
        println("Got Test Msg! It says: $msg")
    }

    override fun getId(): CustomPayload.Id<TestMsg> = ID

    companion object {
        val ID: CustomPayload.Id<TestMsg> = CustomPayload.id("test_msg")
    }
}