package io.ejekta.kambrik.fabric.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.ejekta.kambrik.command.ArgDsl
import io.ejekta.kambrik.command.KambrikCommandApi
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.loader.api.FabricLoader

// Can be called to register a clientside only command
/**
 * Can be used to add a clientside only command to the game
 *
 * @see [ClientCommands]
 *
 * @param baseCommandName The name of the command. The first word you type. e.g. `kambrik` becomes `/kambrik`.
 * @param func The [Command DSL](https://kambrik.ejekta.io/apis/stable/Command.html) describing your function.
 */
fun KambrikCommandApi.addClientCommand(
    baseCommandName: String,
    func: ArgDsl<FabricClientCommandSource, LiteralArgumentBuilder<FabricClientCommandSource>>
) {
    if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
        addSourcedCommand(baseCommandName, ClientCommands.getActiveDispatcher() as CommandDispatcher<FabricClientCommandSource>, func)
    }
}
