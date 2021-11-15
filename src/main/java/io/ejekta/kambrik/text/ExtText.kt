package io.ejekta.kambrik.text

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.MessageType
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import net.minecraft.util.Util

fun ServerCommandSource.sendError(literal: String, text: KambrikTextBuilder<LiteralText>.() -> Unit = {}) {
    sendError(textLiteral(literal, text))
}

fun ServerCommandSource.sendFeedback(literal: String, broadcastToOps: Boolean = false, dsl: KambrikTextBuilder<LiteralText>.() -> Unit = {}) {
    sendFeedback(textLiteral(literal, dsl), broadcastToOps)
}

fun MinecraftServer.broadcast(literal: String = "", messageType: MessageType = MessageType.CHAT, text: KambrikTextBuilder<LiteralText>.() -> Unit) {
    playerManager.broadcast(textLiteral(literal, text), messageType, Util.NIL_UUID)
}

fun PlayerEntity.sendMessage(literal: String = "", vararg formats: Formatting, actionBar: Boolean = false, text: KambrikTextBuilder<LiteralText>.() -> Unit = {}) {
    sendMessage(
        textLiteral(literal) {
            format(*formats)
            apply(text)
        },
        actionBar
    )
}
