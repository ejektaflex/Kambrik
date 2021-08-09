package io.ejekta.kambrik.testing

import io.ejekta.kambrik.api.network.KambrikMessages
import io.ejekta.kambrik.api.network.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

@Serializable
class TestMsg(val num: Int) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        println("Got num!: $num")
    }
}

