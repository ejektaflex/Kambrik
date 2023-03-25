package io.ejekta.kambrik.internal

import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Serializable

@Serializable
data class TestMsg(val msg: String) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        println("Got Test Msg! It says: $msg")
    }
}