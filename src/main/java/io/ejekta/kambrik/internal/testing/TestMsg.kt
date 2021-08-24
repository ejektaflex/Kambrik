package io.ejekta.kambrik.internal.testing

import io.ejekta.kambrik.message.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.item.Item

@Serializable
class TestMsg(@Contextual val item: Item) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        println("Got item! default stack is: ${item.defaultStack}")
    }
}

