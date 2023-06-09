package io.ejekta.kambrik.text

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting

fun ServerCommandSource.sendError(literal: String, text: KambrikTextBuilder<MutableText>.() -> Unit = {}) {
    sendError(textLiteral(literal, text))
}

fun ServerCommandSource.sendFeedback(literal: String, broadcastToOps: Boolean = false, dsl: KambrikTextBuilder<MutableText>.() -> Unit = {}) {
    sendFeedback({ textLiteral(literal, dsl) }, broadcastToOps)
}

fun MinecraftServer.broadcast(literal: String = "", overlay: Boolean = false, text: KambrikTextBuilder<MutableText>.() -> Unit) {
    playerManager.broadcast(textLiteral(literal, text), overlay)
}

fun PlayerEntity.sendMessage(literal: String = "", vararg formats: Formatting, actionBar: Boolean = false, text: KambrikTextBuilder<MutableText>.() -> Unit = {}) {
    sendMessage(
        textLiteral(literal) {
            format(*formats)
            apply(text)
        },
        actionBar
    )
}