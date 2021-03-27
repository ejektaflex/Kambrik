package io.ejekta.kambrik.ext

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity

fun ServerPlayerEntity.getPacketSender(): PacketSender {
    return ServerPlayNetworking.getSender(this)
}

