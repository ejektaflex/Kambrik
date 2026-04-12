package io.ejekta.kambrik.fabric.ext.fapi

import io.netty.buffer.Unpooled
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf

fun Tag.wrapToPacketByteBuf(): FriendlyByteBuf {
    return FriendlyByteBuf(Unpooled.buffer()).apply {
        writeNbt(CompoundTag().apply {
            put("content", this@wrapToPacketByteBuf.copy())
        })
    }
}
