package io.ejekta.kambrik.internal

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.message.KambrikMessageApi
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

@Serializable
data class TestMsg(val msg: String, @Contextual val id: ResourceLocation) : KambrikMsg() {
    override fun onClientReceived() {
        Minecraft.getInstance().player?.sendSystemMessage(Component.literal("Got Test Msg! It says: $msg"))
        println("Got Test Msg! It says: $msg")
    }
}