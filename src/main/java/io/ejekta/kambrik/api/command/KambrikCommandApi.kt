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

    fun hasBasicCreativePermission(c: ServerCommandSource): Boolean {
        return c.hasPermissionLevel(2) || (c.entity is PlayerEntity && c.player.isCreative)
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
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            addSourcedCommand<FabricClientCommandSource>(baseCommandName, ClientCommandManager.DISPATCHER, func)
        }
    }

}