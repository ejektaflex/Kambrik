package io.ejekta.kambrik.api.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

/**
 * Accessed via [Kambrik.Command][io.ejekta.kambrik.Kambrik.Command]
 */
class KambrikCommandApi<SRC : CommandSource> internal constructor() {

    fun hasBasicCreativePermission(c: ServerCommandSource): Boolean {
        return c.hasPermissionLevel(2) || (c.entity is PlayerEntity && c.player.isCreative)
    }

    private fun <SRC : CommandSource> addSourcedCommand(
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

    // Meant to be called from inside of a CommandRegistrationCallback event
    fun addCommand(
        baseCommandName: String,
        toDispatcher: CommandDispatcher<ServerCommandSource>,
        func: ArgDsl<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>>
    ) {
        addSourcedCommand(baseCommandName, toDispatcher, func)
    }

    // Can be called to register a clientside only command
    fun addClientCommand(
        baseCommandName: String,
        func: ArgDsl<FabricClientCommandSource, LiteralArgumentBuilder<FabricClientCommandSource>>
    ) {
        addSourcedCommand<FabricClientCommandSource>(baseCommandName, ClientCommandManager.DISPATCHER, func)
    }

}