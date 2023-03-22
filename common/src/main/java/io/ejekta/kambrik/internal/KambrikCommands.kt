package io.ejekta.kambrik.internal

import com.mojang.brigadier.CommandDispatcher
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.command.addCommand
import io.ejekta.kambrik.command.kambrikServerCommand
import io.ejekta.kambrik.command.suggestionList
import io.ejekta.kambrik.text.sendError
import io.ejekta.kambrik.text.sendFeedback
import io.ejekta.kambrik.text.textLiteral
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.registry.Registries
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

object KambrikCommands {
    fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>
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

            if (Kambridge.side == BridgeSide.FABRIC) {
                if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                    "test" {
                        "text" runs text()
                        argString("doot") { doot ->
                            this runs {
                                println(doot())
                            }
                        }
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
        val test = textLiteral("Hello World!") {
            onHoverShowText {
                format(Formatting.ITALIC)
                addLiteral("How are you?")
            }
        }
        source.sendFeedback(test, false)
    }

}