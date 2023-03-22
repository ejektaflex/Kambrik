package io.ejekta.kambrik.internal

import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable

@Serializable
data class TestMsg(val text: String) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        println("Client got msg! It says: $text")
    }
}