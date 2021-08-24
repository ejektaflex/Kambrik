package io.ejekta.kambrik.internal.testing

import io.ejekta.kambrik.message.ServerMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos

@Serializable
class TellServerHello(val pos: @Contextual BlockPos) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        println("Hello from $pos!")
    }
}




