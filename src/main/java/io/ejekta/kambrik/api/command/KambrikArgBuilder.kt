package io.ejekta.kambrik.api.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType.bool
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.IdentifierArgumentType.identifier
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class KambrikArgBuilder<SRC : CommandSource, A : ArgumentBuilder<SRC, *>>(val arg: A) :
    ArgumentBuilder<SRC, KambrikArgBuilder<SRC, A>>() {

    private val subArgs = mutableListOf<KambrikArgBuilder<SRC, *>>()

    internal fun finalize(): A {
        for (subArg in subArgs) {
            arg.then(subArg) // used to be `arg.then(subArg.arg)`, changed when refactored for agnostic command source
        }
        return arg
    }

    fun literal(word: String, func: ArgDsl<SRC, LiteralArgumentBuilder<SRC>> = {}): LiteralArgumentBuilder<SRC> {
        val req = KambrikArgBuilder<SRC, LiteralArgumentBuilder<SRC>>(LiteralArgumentBuilder.literal(word)).apply(func)
        req.finalize()
        subArgs.add(req)
        return req.arg
    }

    fun <T> argument(
        type: ArgumentType<T>,
        word: String,
        items: SuggestionProvider<SRC>? = null,
        func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, *>> = {}
    ): RequiredArgumentBuilder<SRC, *> {
        val req = KambrikArgBuilder<SRC, RequiredArgumentBuilder<SRC, *>>(RequiredArgumentBuilder.argument(word, type)).apply(func)

        items?.let {
            req.arg.suggests(it)
        }

        req.finalize()
        subArgs.add(req)
        return req.arg
    }



    fun argString(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, *>> = {}
    ) = argument(string(), word, items, func)

    fun argInt(
        word: String, range: IntRange? = null,
        items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, *>> = {}
    ) = argument(if (range != null) integer(range.first, range.last) else integer(), word, items, func)

    fun argFloat(
        word: String, range: ClosedFloatingPointRange<Float>? = null,
        items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, *>> = {}
    ) = argument(if (range != null) FloatArgumentType.floatArg(range.start, range.endInclusive) else FloatArgumentType.floatArg(), word, items, func)

    fun argBool(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, *>> = {}
    ) = argument(bool(), word, items, func)

    fun argIdentifier(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, *>> = {}
    ) = argument(identifier(), word, items, func)

    operator fun String.invoke(func: ArgDsl<SRC, LiteralArgumentBuilder<SRC>>) {
        literal(this, func)
    }

    @Suppress("UNCHECKED_CAST")
    override fun executes(command: Command<SRC>?): KambrikArgBuilder<SRC, A> {
        return KambrikArgBuilder(arg.executes(command) as A)
    }

    infix fun String.runs(cmd: Command<SRC>) {
        this { this.executes(cmd) }
    }

    infix fun runs(cmd: Command<SRC>) {
        executes(cmd)
    }

    infix fun RequiredArgumentBuilder<SRC, *>.runs(cmd: Command<SRC>) {
        this@runs.executes(cmd)
    }

    infix fun LiteralArgumentBuilder<SRC>.runs(cmd: Command<SRC>) {
        this@runs.executes(cmd)
    }

    override fun getThis(): KambrikArgBuilder<SRC, A> = this

    override fun build(): CommandNode<SRC> {
        return arg.build()
    }

}