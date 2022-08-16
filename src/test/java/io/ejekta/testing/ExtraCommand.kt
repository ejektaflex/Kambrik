package io.ejekta.testing

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.command.KCommand
import io.ejekta.kambrik.command.KambrikArgBuilder
import io.ejekta.kambrik.command.addCommand
import io.ejekta.kambrik.internal.KambrikCommands
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource

fun interface NewCommand<SRC : CommandSource> {
    fun apply(cmd: KambrikArgBuilder<SRC, *>)
}

val newCommandA = NewCommand<ServerCommandSource> {
    it.run {
        "do_something" runs {
            println("Did something!")
        }
    }
}

class NewCommandType() : NewCommand<ServerCommandSource> {
    override fun apply(cmd: KambrikArgBuilder<ServerCommandSource, *>) {
        cmd.run {
            "do_other_thing" runs {
                println("Did something else")
            }
        }
    }
}

fun main() { /* onInitialize */
    CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, dedicated, env ->
        dispatcher.addCommand("test") {
            "apples" runs { println("Apples") }
            newCommandA.apply(this)
            NewCommandType().apply(this)
        }
    })
}