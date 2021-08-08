package io.ejekta.kambrik.testing

import io.ejekta.kambrik.api.network.client.ClientMsg
import io.ejekta.kambrik.api.network.client.ClientMsgHandler
import kotlinx.serialization.Serializable
import net.minecraft.util.Identifier

@Serializable
class TestMsg(val num: Int) : ClientMsg<TestMsg>(Handler) {

    override fun onClientReceived(ctx: ClientMsgContext) {
        println("Got num!: $num")
    }

    companion object {
        val Handler = ClientMsgHandler(
            Identifier("a", "b") to { TestMsg.serializer() }
        )
    }
}

