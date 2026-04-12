package io.ejekta.kambrik.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.Message
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.commands.addAll
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.permissions.Permission
import net.minecraft.server.permissions.PermissionLevel
import net.minecraft.world.entity.player.Player

typealias ArgDsl<S, T> = KambrikArgBuilder<S, T>.() -> Unit
typealias ArgDslTyped<S, A> = KambrikArgBuilder<S, RequiredArgumentBuilder<S, A>>.(it: CommandContext<S>.() -> A) -> Unit
typealias KCommand<SRC> = CommandContext<SRC>.() -> Unit // non-int return type command

/**
 * Command registration shortcut
 * A simple alternate for calling [Kambrik.Command]'s addSourcedCommand
 */
fun CommandDispatcher<CommandSourceStack>.addCommand(
    baseCommandName: String,
    func: ArgDsl<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>>
) {
    Kambrik.Command.addSourcedCommand(baseCommandName, this, func)
}

// Suggestion list providers

/**
 * Creates a suggestion provider from a list of objects
 * @param func A producer of said list of objects
 */
fun <SRC : CommandSourceStack> KambrikArgBuilder<SRC, *>.suggestionList(func: () -> List<Any>): SuggestionProvider<SRC> {
    return SuggestionProvider<SRC> { context, builder ->
        builder.addAll(func().map { it.toString() })
        builder.buildFuture()
    }
}

/**
 * Creates a tooltipped suggestion provider from a list of pairs of string and message.
 * Each string, when hovered, will show the corresponding tooltip message.
 * @param func A producer of the list of pairs
 */
fun <SRC : CommandSourceStack> KambrikArgBuilder<SRC, *>.suggestionListTooltipped(func: () -> List<Pair<String, Message>>): SuggestionProvider<SRC> {
    return SuggestionProvider<SRC> { _, builder ->
        for ((item, msg) in func()) {
            builder.suggest(item, msg)
        }
        builder.buildFuture()
    }
}

/* Creates a non-int return type command */
/**
 * Creates a Kambrik command. These commands do not return an integer, unlike
 * regular commands.
 */
fun <SRC : CommandSourceStack> kambrikCommand(func: KCommand<SRC>): Command<SRC> {
    return Command<SRC> {
        it.func()
        1
    }
}

fun kambrikServerCommand(func: KCommand<CommandSourceStack>): Command<CommandSourceStack> {
    return kambrikCommand(func)
}

/* Shortcuts for requirements */

/**
 * Used to ensure that a command requires creative mode permissions to execute
 */
fun KambrikArgBuilder<CommandSourceStack, *>.requiresCreative() {
    requires { it.entity is Player && it.player?.isCreative == true }
}

/**
 * Used to ensure that a command requires a specific op level or higher to execute
 */
fun KambrikArgBuilder<CommandSourceStack, *>.requiresOp(opLevel: Int = 4) {
    requires { it.permissions().hasPermission(Permission.HasCommandLevel(PermissionLevel.byId(opLevel))) }
}

/**
 * Used to ensure that a command requires creative mode or a specific op level or higher to execute
 */
fun KambrikArgBuilder<CommandSourceStack, *>.requiresCreativeOrOp(opLevel: Int = 4) {
    requires { (it.entity is Player && it.player?.isCreative == true) || it.permissions().hasPermission(Permission.HasCommandLevel(PermissionLevel.byId(opLevel))) }
}


