package io.ejekta.kambrik.internal

import com.mojang.brigadier.CommandDispatcher
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.command.*
import io.ejekta.kambrik.text.sendError
import io.ejekta.kambrik.text.sendFeedback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.registry.Registries
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

object KambrikCommands {
    fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        registryAccess: CommandRegistryAccess,
        environment: CommandManager.RegistrationEnvironment
    ) {

        dispatcher.addCommand(Kambrik.ID) {

            "dump" {
                "registry" {
                    val dumpables = suggestionList { Registries.REGISTRIES.toList().map { it.key.value } }
                    argIdentifier("dump_what", items = dumpables) runs { what ->
                        dumpRegistry(what()).run(this)
                    }
                }
            }

            "test" {
                "text" runs text()
                argString("doot") { doot ->
                    this runs {
                        println(doot())
                    }
                }
                "net" runs {
                    try {
                        TestMsg("net send here").sendToClient(source.playerOrThrow)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


    }


    private fun dumpRegistry(what: Identifier) = kambrikServerCommand {
        if (Registries.REGISTRIES.containsId(what)) {
            val reg = Registries.REGISTRIES[what]!!
            Kambrik.Logger.info("Contents of registry '$what':")
            reg.ids.forEach { id ->
                Kambrik.Logger.info("  * [ID] $id")
            }
            source.sendFeedback("Dumped contents of '$what' to log.")
        } else {
            source.sendError("There is no registry with that name.")
        }
    }

    private fun text() = kambrikServerCommand {
        val test = Text.literal("Hello World!")
        source.sendFeedback({ test }, false)
    }

}