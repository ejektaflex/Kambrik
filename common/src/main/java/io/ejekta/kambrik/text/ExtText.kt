package io.ejekta.kambrik.text

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

fun ServerCommandSource.sendError(literal: String, textDsl: MutableText.() -> Unit = {}) {
    sendError(Text.literal(literal).apply(textDsl))
}

fun ServerCommandSource.sendFeedback(literal: String, broadcastToOps: Boolean = false, dsl: MutableText.() -> Unit = {}) {
    sendFeedback({ Text.literal(literal).apply(dsl) }, broadcastToOps)
}

fun MinecraftServer.broadcast(literal: String = "", overlay: Boolean = false, text: MutableText.() -> Unit) {
    playerManager.broadcast(Text.literal(literal).apply(text), overlay)
}

fun PlayerEntity.sendMessage(literal: String = "", vararg formats: Formatting, actionBar: Boolean = false, text: MutableText.() -> Unit = {}) {
    sendMessage(
        Text.literal(literal).formatted(*formats).apply(text),
        actionBar
    )
}