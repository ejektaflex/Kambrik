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
    ### To register the message:
    KambrikMessages.registerClientMessage(TestMsg.serializer(), Identifier("kambrik", "test_msg"))

    ### To use the message:
    TestMsg(100).sendTo(some_player)

    ### We can also automatically serialize some Minecraft data classes. you do it like so:
    class AnotherTestMsg(@Contextual val pos: BlockPos)
 */
