package io.ejekta.kambrik.testing

import io.ejekta.kambrik.api.message.ClientMsg
import io.ejekta.kambrik.api.message.ServerMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos

@Serializable
class TellServerHello(@Contextual val pos: BlockPos) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        println("Hello! Got item! default stack is: $pos")
    }
}

