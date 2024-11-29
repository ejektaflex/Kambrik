package io.ejekta.kambrik.command.types

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.ejekta.kambrik.text.sendFailure
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer

/**
 * This represents a command that is run by a server player.
 * A useful shortcut since most commands take a player input.
 *
 * @param func The command context, giving also a player parameter
 */
class PlayerCommand(val func: CommandContext<CommandSourceStack>.(player: ServerPlayer) -> Int) : Command<CommandSourceStack> {

    override fun run(ctx: CommandContext<CommandSourceStack>): Int {
        val from = ctx.source.entity
        if (ctx.source !is CommandSourceStack) { // TODO reference player
            ctx.source.sendFailure("Only Players can send Player commands.")
        }
        return func(ctx, ctx.source.player!!)
    }

}