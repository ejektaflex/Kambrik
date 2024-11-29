package io.ejekta.kambrik.fabric.ext.fapi

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer

fun ServerPlayer.getPacketSender(): PacketSender {
    return ServerPlayNetworking.getSender(this)
}

