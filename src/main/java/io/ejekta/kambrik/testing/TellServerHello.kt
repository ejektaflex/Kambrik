package io.ejekta.kambrik.testing

import io.ejekta.kambrik.api.message.ClientMsg
import io.ejekta.kambrik.api.message.ServerMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.item.Item

@Serializable
class TellServerHello(@Contextual val item: Item) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        println("Got item! default stack is: ${item.defaultStack}")
    }
}

