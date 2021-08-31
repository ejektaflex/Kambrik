package io.ejekta.kambrik.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType.bool
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.ColorArgumentType
import net.minecraft.command.argument.IdentifierArgumentType.identifier
import net.minecraft.command.argument.NumberRangeArgumentType.intRange
import net.minecraft.command.argument.PosArgument
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.predicate.NumberRange
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

/**
 * Nearly the entirety of Kambrik's Command DSL
 * @see [Command DSL Docs](https://kambrik.ejekta.io/apis/stable/Command)
 */
class KambrikArgBuilder<SRC, A : ArgumentBuilder<SRC, *>>(val arg: A) :
    ArgumentBuilder<SRC, KambrikArgBuilder<SRC, A>>() {

    private val subArgs = mutableListOf<KambrikArgBuilder<SRC, *>>()

    internal fun finalize(): A {
        for (subArg in subArgs) {
            arg.then(subArg) // used to be `arg.then(subArg.arg)`, changed when refactored for agnostic command source
        }
        return arg
    }

    /**
     * Specifies a literal argument.
     */
    fun literal(word: String, func: ArgDsl<SRC, LiteralArgumentBuilder<SRC>> = {}): LiteralArgumentBuilder<SRC> {
        val req = KambrikArgBuilder<SRC, LiteralArgumentBuilder<SRC>>(LiteralArgumentBuilder.literal(word)).apply(func)
        req.finalize()
        subArgs.add(req)
        return req.arg
    }

    /**
     * Specifies a generic required argument.
     */
    fun <ARG> argument(
        type: ArgumentType<ARG>,
        word: String,
        items: SuggestionProvider<SRC>? = null,
        func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, ARG>> = {}
    ): RequiredArgumentBuilder<SRC, ARG> {
        val req = KambrikArgBuilder<SRC, RequiredArgumentBuilder<SRC, ARG>>(RequiredArgumentBuilder.argument(word, type)).apply(func)

        items?.let {
            req.arg.suggests(it)
        }

        req.finalize()
        subArgs.add(req)
        return req.arg
    }

    fun argBlockPos(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, PosArgument>> = {}
    ) = argument(BlockPosArgumentType.blockPos(), word, items, func)

    fun argBool(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, Boolean>> = {}
    ) = argument(bool(), word, items, func)

    fun argColor(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, Formatting>> = {}
    ) = argument(ColorArgumentType.color(), word, items, func)

    fun argFloat(
        word: String, range: ClosedFloatingPointRange<Float>? = null,
        items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, Float>> = {}
    ) = argument(if (range != null) FloatArgumentType.floatArg(range.start, range.endInclusive) else FloatArgumentType.floatArg(), word, items, func)

    fun argIdentifier(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, Identifier>> = {}
    ) = argument(identifier(), word, items, func)

    fun argInt(
        word: String, range: IntRange? = null,
        items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, Int>> = {}
    ) = argument(if (range != null) integer(range.first, range.last) else integer(), word, items, func)

    fun argIntRange(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, NumberRange.IntRange>> = {}
    ) = argument(intRange(), word, items, func)

    fun argString(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDsl<SRC, RequiredArgumentBuilder<SRC, String>> = {}
    ) = argument(string(), word, items, func)

    /**
     * A shortcut for creating a literal argument.
     * @see [literal]
     */
    operator fun String.invoke(func: ArgDsl<SRC, LiteralArgumentBuilder<SRC>>) {
        literal(this, func)
    }


    @Suppress("UNCHECKED_CAST")
    override fun executes(command: Command<SRC>?): KambrikArgBuilder<SRC, A> {
        return KambrikArgBuilder(arg.executes(command) as A)
    }

    /**
     * Specifies a command to execute when the string literal is called.
     */
    infix fun String.runs(cmd: Command<SRC>) {
        this { this.executes(cmd) }
    }

    // Alternate shortcuts for `runs`.

    infix fun runs(cmd: Command<SRC>) {
        executes(cmd)
    }

    infix fun RequiredArgumentBuilder<SRC, *>.runs(cmd: Command<SRC>) {
        this@runs.executes(cmd)
    }

    infix fun LiteralArgumentBuilder<SRC>.runs(cmd: Command<SRC>) {
        this@runs.executes(cmd)
    }

    inline fun <reified ARG : Any> RequiredArgumentBuilder<SRC, ARG>.runsArg(crossinline func: (it: ARG) -> Command<SRC>) {
        this runs Command {
            val gotArg = it.getArgument(this.name, ARG::class.java)
            func(gotArg).run(it)
            1
        }
    }

    inline fun <reified ARG : Any> RequiredArgumentBuilder<SRC, ARG>.block(crossinline func: (it: ARG) -> Command<SRC>) {
        this runs Command {
            val gotArg = it.getArgument(this.name, ARG::class.java)
            func(gotArg).run(it)
            1
        }
    }


    override fun getThis(): KambrikArgBuilder<SRC, A> = this

    override fun build(): CommandNode<SRC> {
        return arg.build()
    }

}