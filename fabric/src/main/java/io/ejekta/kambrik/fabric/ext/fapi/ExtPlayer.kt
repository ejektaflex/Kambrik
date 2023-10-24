package io.ejekta.kambrik.fabric.ext.fapi

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.server.network.ServerPlayerEntity

fun ServerPlayerEntity.getPacketSender(): PacketSender {
    return ServerPlayNetworking.getSender(this)
}

