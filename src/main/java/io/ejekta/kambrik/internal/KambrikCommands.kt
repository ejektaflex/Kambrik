package io.ejekta.kambrik.internal

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.ejekta.kambrik.command.KambrikArgBuilder
import io.ejekta.kambrik.command.addCommand
import io.ejekta.kambrik.command.kambrikServerCommand
import io.ejekta.kambrik.command.suggestionList
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kambrik.text.sendError
import io.ejekta.kambrik.text.sendFeedback
import io.ejekta.kambrik.text.textLiteral
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.tag.*
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

internal object KambrikCommands : CommandRegistrationCallback {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean) {

        dispatcher.addCommand(KambrikMod.ID) {

            "dump" {
                "registry" {
                    val dumpables = suggestionList { Registry.REGISTRIES.toList().map { it.key.value } }
                    argIdentifier("dump_what", items = dumpables) runs { what ->
                        dumpRegistry(what()).run(this)
                    }
                }
            }

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


    private fun dumpRegistry(what: Identifier) = kambrikServerCommand {
        if (Registry.REGISTRIES.containsId(what)) {
            val reg = Registry.REGISTRIES[what]!!
            KambrikMod.Logger.info("Contents of registry '$what':")
            reg.ids.forEach { id ->
                KambrikMod.Logger.info("  * [ID] $id")
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