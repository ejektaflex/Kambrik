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

fun CommandDispatcher<ServerCommandSource>.addCommand(
    baseCommandName: String,
    func: ArgDsl<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>>
) {
    Kambrik.Command.addSourcedCommand(baseCommandName, this, func)
}

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

inline fun <SRC : CommandSource, reified ARG_A : Any, reified ARG_B : Any> KambrikArgBuilder<SRC, *>.runArgs(
    argB: KambrikArgBuilder<SRC, RequiredArgumentBuilder<SRC, ARG_B>>,
    argA: KambrikArgBuilder<SRC, RequiredArgumentBuilder<SRC, ARG_A>>,
    crossinline func: (ctx: CommandContext<SRC>, a: ARG_A, b: ARG_B) -> Int
) {
    this runs Command {
        val gotArgA = it.getArgument(argA.arg.name, ARG_A::class.java)
        val gotArgB = it.getArgument(argB.arg.name, ARG_B::class.java)
        func(it, gotArgA, gotArgB)
    }
}

/*
inline fun <SRC : CommandSource, reified ARG, REQ : RequiredArgumentBuilder<SRC, ARG>> KambrikArgBuilder<SRC, REQ>.argFunc(): CommandContext<SRC>.() -> ARG {
    return {
        getArgument(this@argFunc.arg.name, ARG::class.java)
    }
}

 */

//*
inline fun <SRC, REQ : RequiredArgumentBuilder<SRC, ARG>, reified ARG> KambrikArgBuilder<SRC, REQ>.argFunc(): CommandContext<SRC>.() -> ARG {
    return {
        getArgument(this@argFunc.arg.name, ARG::class.java)
    }
}

 //*/

fun playerCommand(player: CommandContext<ServerCommandSource>.(player: ServerPlayerEntity) -> Int): PlayerCommand {
    return PlayerCommand(player)
}

// Simple contextual arg getters
fun <SRC : CommandSource> CommandContext<SRC>.getString(name: String): String = StringArgumentType.getString(this, name)
fun <SRC : CommandSource> CommandContext<SRC>.getInt(name: String): Int = IntegerArgumentType.getInteger(this, name)

fun KambrikArgBuilder<ServerCommandSource, *>.requiresCreative() {
    requires { it.entity is PlayerEntity && it.player.isCreative }
}

fun KambrikArgBuilder<ServerCommandSource, *>.requiresOp(opLevel: Int = 4) {
    requires { it.hasPermissionLevel(opLevel) }
}

fun KambrikArgBuilder<ServerCommandSource, *>.requiresCreativeOrOp(opLevel: Int = 4) {
    requires { (it.entity is PlayerEntity && it.player.isCreative) || it.hasPermissionLevel(opLevel) }
}

