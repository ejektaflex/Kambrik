package io.ejekta.kambrik.testing

import io.ejekta.kambrik.api.network.client.ClientMsg
import io.ejekta.kambrik.api.network.client.ClientNetworkLink
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import net.minecraft.util.Identifier

@Serializable
class TestMsg(val num: Int) : ClientMsg<TestMsg>() {

    override fun onClientReceived(ctx: ClientMsgContext) {
        println("Got num!: $num")
    }
}

/*

We need: Serializer for ser/deser on both ends
We need: Identifier for sending/reg receiver, NOT for serialization


network.link<TestMsg>( TestMsg.serializer(), Identifier("a", "b") )

network.send(TestMsg(100))
// or
TestMsg(100).sendToClient()

network holds the packetinfo for ser/deser

WHEN SENDING, look up the sent class and grab it's serializer



 */