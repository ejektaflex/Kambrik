package io.ejekta.kambrik.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

/**
 * Accessed via [Kambrik.Command][io.ejekta.kambrik.Kambrik.Command]
 */
class KambrikCommandApi internal constructor() {

    /**
     * Can be used to add a normal (serverside) command to the game.
     *
     * @param baseCommandName The name of the command. The first word you type. e.g. `kambrik` becomes `/kambrik`.
     * @param toDispatcher The dispatcher of this command. This is provided in a [CommandRegistrationCallback][net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback]
     * @param func The [Command DSL](https://kambrik.ejekta.io/apis/stable/Command.html) describing your function.
     */
    fun addCommand(
        baseCommandName: String,
        toDispatcher: CommandDispatcher<ServerCommandSource>,
        func: ArgDsl<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>>
    ) {
        addSourcedCommand(baseCommandName, toDispatcher, func)
    }

    fun <SRC> addSourcedCommand(
        baseCommandName: String,
        toDispatcher: CommandDispatcher<SRC>,
        func: ArgDsl<SRC, LiteralArgumentBuilder<SRC>>
    ) {
        toDispatcher.register(
            KambrikArgBuilder(
                LiteralArgumentBuilder.literal<SRC>(baseCommandName)
            ).apply(func).finalize()
        )
    }
}