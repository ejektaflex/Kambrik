package io.ejekta.kambrik.fabric.ext.fapi

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf


fun NbtElement.wrapToPacketByteBuf(): PacketByteBuf {
    return PacketByteBufs.create().apply {
        writeNbt(NbtCompound().apply {
            put("content", this@wrapToPacketByteBuf.copy())
        })
    }
}