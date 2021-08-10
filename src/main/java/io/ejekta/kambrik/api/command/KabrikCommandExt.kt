package io.ejekta.kambrik.api.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.Message
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.api.command.types.PlayerCommand
import io.ejekta.kambrik.ext.addAll
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

typealias ArgDsl<S, T> = KambrikArgBuilder<S, T>.() -> Unit

fun <SRC : CommandSource> CommandDispatcher<SRC>.addCommand(
    baseCommandName: String,
    func: ArgDsl<SRC, LiteralArgumentBuilder<SRC>>
) {
    Kambrik.Command.addSourcedCommand(baseCommandName, this, func)
}

fun <SRC : CommandSource> KambrikArgBuilder<SRC, *>.suggestionList(func: () -> List<String>): SuggestionProvider<SRC> {
    return SuggestionProvider<SRC> { context, builder ->
        builder.addAll(func())
        builder.buildFuture()
    }
}

fun <SRC : CommandSource> KambrikArgBuilder<SRC, *>.suggestionListTooltipped(func: () -> List<Pair<String, Message>>): SuggestionProvider<SRC> {
    return SuggestionProvider<SRC> { _, builder ->
        for ((item, msg) in func()) {
            builder.suggest(item, msg)
        }
        builder.buildFuture()
    }
}

fun playerCommand(player: CommandContext<ServerCommandSource>.(player: ServerPlayerEntity) -> Int): PlayerCommand {
    return PlayerCommand(player)
}

fun <SRC : CommandSource> CommandContext<SRC>.getString(name: String): String = StringArgumentType.getString(this, name)
fun <SRC : CommandSource> CommandContext<SRC>.getInt(name: String): Int = IntegerArgumentType.getInteger(this, name)

