package io.ejekta.kambrik.testing

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.api.message.ClientMsg
import io.ejekta.kambrik.api.message.ServerMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

@Serializable
class TellServerHello(val pos: @Contextual BlockPos) : ServerMsg() {
    override fun onServerReceived(ctx: MsgContext) {
        println("Hello from $pos!")
    }
}




