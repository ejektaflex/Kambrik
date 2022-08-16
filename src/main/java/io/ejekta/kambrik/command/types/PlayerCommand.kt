package io.ejekta.kambrik.command.types

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.ejekta.kambrik.text.sendError
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

/**
 * This represents a command that is run by a server player.
 * A useful shortcut since most commands take a player input.
 *
 * @param func The command context, giving also a player parameter
 */
class PlayerCommand(val func: CommandContext<ServerCommandSource>.(player: ServerPlayerEntity) -> Int) : Command<ServerCommandSource> {

    override fun run(ctx: CommandContext<ServerCommandSource>): Int {
        val from = ctx.source.entity
        if (ctx.source !is ServerCommandSource) { // TODO reference player
            ctx.source.sendError("Only Players can send Player commands.")
        }
        return func(ctx, ctx.source.player!!) // we'll fix this later if need be
    }

}