package io.ejekta.kambrik.api.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
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

    // Can be called to register a clientside only command
    /**
     * Can be used to add a clientside only command to the game
     *
     * @see [ClientCommandManager]
     *
     * @param baseCommandName The name of the command. The first word you type. e.g. `kambrik` becomes `/kambrik`.
     * @param toDispatcher The dispatcher of this command. This is provided in a [CommandRegistrationCallback][net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback]
     * @param func The [Command DSL](https://kambrik.ejekta.io/apis/stable/Command.html) describing your function.
     */
    fun addClientCommand(
        baseCommandName: String,
        func: ArgDsl<FabricClientCommandSource, LiteralArgumentBuilder<FabricClientCommandSource>>
    ) {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            addSourcedCommand<FabricClientCommandSource>(baseCommandName, ClientCommandManager.DISPATCHER, func)
        }
    }

    internal fun <SRC> addSourcedCommand(
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