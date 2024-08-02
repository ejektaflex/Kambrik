package io.ejekta.kambrik.internal

import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

@Serializable
data class TestMsg(val msg: String, @Contextual val id: Identifier) : KambrikMsg() {
    override fun onClientReceived() {
        println("Got Test Msg! It says: $msg")
    }

    override fun getId(): CustomPayload.Id<TestMsg> = ID

    companion object {
        val ID: CustomPayload.Id<TestMsg> = CustomPayload.id("test_msg")
    }
}