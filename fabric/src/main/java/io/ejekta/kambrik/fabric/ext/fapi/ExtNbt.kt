package io.ejekta.kambrik.fabric.ext.fapi

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf

fun Tag.wrapToPacketByteBuf(): FriendlyByteBuf {
    return PacketByteBufs.create().apply {
        writeNbt(CompoundTag().apply {
            put("content", this@wrapToPacketByteBuf.copy())
        })
    }
}