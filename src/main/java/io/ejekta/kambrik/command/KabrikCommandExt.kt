package io.ejekta.kambrik.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.Message
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.command.commands.PlayerCommand
import io.ejekta.kambrik.ext.addAll
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

typealias ArgDsl<S, T> = KambrikArgBuilder<S, T>.() -> Unit
typealias ArgDslTyped<S, A> = KambrikArgBuilder<S, RequiredArgumentBuilder<S, A>>.(it: CommandContext<S>.() -> A) -> Unit
typealias KCommand<SRC> = CommandContext<SRC>.() -> Unit // non-int return type command

// Command registration shortcut
fun CommandDispatcher<ServerCommandSource>.addCommand(
    baseCommandName: String,
    func: ArgDsl<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>>
) {
    Kambrik.Command.addSourcedCommand(baseCommandName, this, func)
}

// Suggestion list providers

fun <SRC : CommandSource> KambrikArgBuilder<SRC, *>.suggestionList(func: () -> List<Any>): SuggestionProvider<SRC> {
    return SuggestionProvider<SRC> { context, builder ->
        builder.addAll(func().map { it.toString() })
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

/* Creates a non-int return type command */
fun <SRC : CommandSource> kambrikCommand(func: KCommand<SRC>): Command<SRC> {
    return Command<SRC> {
        it.func()
        1
    }
}

/* Shortcuts for requirements */

fun KambrikArgBuilder<ServerCommandSource, *>.requiresCreative() {
    requires { it.entity is PlayerEntity && it.player.isCreative }
}

fun KambrikArgBuilder<ServerCommandSource, *>.requiresOp(opLevel: Int = 4) {
    requires { it.hasPermissionLevel(opLevel) }
}

fun KambrikArgBuilder<ServerCommandSource, *>.requiresCreativeOrOp(opLevel: Int = 4) {
    requires { (it.entity is PlayerEntity && it.player.isCreative) || it.hasPermissionLevel(opLevel) }
}


