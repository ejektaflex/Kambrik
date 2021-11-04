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
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.ColorArgumentType
import net.minecraft.command.argument.IdentifierArgumentType.identifier
import net.minecraft.command.argument.NumberRangeArgumentType.intRange
import net.minecraft.command.argument.PosArgument
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
    private inline fun <reified ARG> argument(
        type: ArgumentType<ARG>,
        word: String,
        items: SuggestionProvider<SRC>? = null,
        func: ArgDslTyped<SRC, ARG> = {}
    ): RequiredArgumentBuilder<SRC, ARG> {
        val req = KambrikArgBuilder<SRC, RequiredArgumentBuilder<SRC, ARG>>(RequiredArgumentBuilder.argument(word, type)).apply {
            func { getArgument(this@apply.arg.name, ARG::class.java) }
        }

        items?.let {
            req.arg.suggests(it)
        }

        req.finalize()
        subArgs.add(req)
        return req.arg
    }
    fun argBlockPos(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDslTyped<SRC, PosArgument> = {}
    ) = argument(BlockPosArgumentType.blockPos(), word, items, func)

    fun argBool(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDslTyped<SRC, Boolean> = {}
    ) = argument(bool(), word, items, func)

    fun argColor(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDslTyped<SRC, Formatting> = {}
    ) = argument(ColorArgumentType.color(), word, items, func)

    fun argFloat(
        word: String, range: ClosedFloatingPointRange<Float>? = null,
        items: SuggestionProvider<SRC>? = null, func: ArgDslTyped<SRC, Float> = {}
    ) = argument(if (range != null) FloatArgumentType.floatArg(range.start, range.endInclusive) else FloatArgumentType.floatArg(), word, items, func)

    fun argIdentifier(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDslTyped<SRC, Identifier> = {}
    ) = argument(identifier(), word, items, func)

    fun argInt(
        word: String, range: IntRange? = null,
        items: SuggestionProvider<SRC>? = null, func: ArgDslTyped<SRC, Int> = {}
    ) = argument(if (range != null) integer(range.first, range.last) else integer(), word, items, func)

    fun argIntRange(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDslTyped<SRC, NumberRange.IntRange> = {}
    ) = argument(intRange(), word, items, func)

    fun argString(
        word: String, items: SuggestionProvider<SRC>? = null, func: ArgDslTyped<SRC, String> = {}
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

    fun executes(kCommand: KCommand<SRC>) {
        executes(Command<SRC> {
            kCommand(it)
            1
        })
    }

    // Alternate shortcuts for `runs`.

    /**
     * Specifies a command to execute when the string literal is called.
     */
    infix fun String.runs(kcmd: KCommand<SRC>) = this { executes(kcmd) }
    infix fun String.runs(cmd: Command<SRC>) = this { executes(cmd) }

    // this runs (required and literal variants)

    inline infix fun <reified ARG> KambrikArgBuilder<SRC, RequiredArgumentBuilder<SRC, ARG>>
            .runs(noinline cmd: CommandContext<SRC>.(it: CommandContext<SRC>.() -> ARG) -> Unit) {
        arg.runs(cmd)
    }

    infix fun KambrikArgBuilder<SRC, LiteralArgumentBuilder<SRC>>.runs(
        cmd: KCommand<SRC>
    ) {
        arg.runs(cmd)
    }

    infix fun KambrikArgBuilder<SRC, LiteralArgumentBuilder<SRC>>.runs(
        cmd: Command<SRC>
    ) {
        arg.runs(cmd)
    }

    //infix fun runs(cmd: Command<SRC>) = executes(cmd)

    // Required Args runs

    inline fun <reified ARG> CommandContext<SRC>.argFrom(req: RequiredArgumentBuilder<SRC, ARG>): ARG {
        return getArgument(req.name, ARG::class.java)
    }

    inline infix fun <reified ARG> RequiredArgumentBuilder<SRC, ARG>.runs(noinline cmd: CommandContext<SRC>.(it: CommandContext<SRC>.() -> ARG) -> Unit) {
        val runContext: CommandContext<SRC>.() -> ARG = {
            getArgument(name, ARG::class.java)
        }

        executes {
            it.cmd(runContext)
            1
        }
    }

    inline infix fun <reified ARG> RequiredArgumentBuilder<SRC, ARG>.runs(cmd: Command<SRC>) {
        executes(cmd)
    }

    // Literal Args runs

    infix fun LiteralArgumentBuilder<SRC>.runs(kcmd: KCommand<SRC>) {
        executes {
            kcmd(it)
            1
        }
    }

    infix fun LiteralArgumentBuilder<SRC>.runs(cmd: Command<SRC>) {
        executes(cmd)
    }

    // Misc builder methods

    override fun getThis(): KambrikArgBuilder<SRC, A> = this

    override fun build(): CommandNode<SRC> {
        return arg.build()
    }

}

