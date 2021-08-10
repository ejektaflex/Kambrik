package io.ejekta.kambrik.internal

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.StringArgumentType.getString
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.KambrikMod
import io.ejekta.kambrik.api.command.suggestionList
import io.ejekta.kambrik.api.logging.KambrikMarkers
import io.ejekta.kambrik.testing.TestMsg
//import io.ejekta.kambrik.testing.TestMsg
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.argument.IdentifierArgumentType.getIdentifier
import net.minecraft.item.Items
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.registry.Registry

internal object KambrikCommands : CommandRegistrationCallback {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean) {
        Kambrik.Command.addCommand(KambrikMod.ID, dispatcher) {
            "logging" {
                "markers" {
                    val currMarkers = suggestionList { KambrikMarkers.Registry.map { "\"" + it.key + "\"" }.sorted() }
                    argString("marker", items = currMarkers) {
                        "set" {
                            argBool("enabled") runs {
                                val marker = getString(it, "marker")
                                val enabled = getBool(it, "enabled")
                                /*
                                KambrikMod.config.edit {
                                    if (enabled == KambrikMarkers.Registry[marker]) {
                                        markers.remove(marker)
                                    } else {
                                        markers[marker] = enabled
                                    }
                                }

                                 */
                                KambrikMod.configureLoggerFilters()
                                1
                            }
                        }
                    }
                }

            }

            "dump" {
                val dumpables = suggestionList { Registry.REGISTRIES.toList().map { it.key.value.toString() } }
                argIdentifier("dump_what", items = dumpables) runs dump()
            }

            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                "test" {
                    "output" runs {
                        Kambrik.Logger.info(KambrikMarkers.General, "Hello! General")
                        Kambrik.Logger.info(KambrikMarkers.Rendering, "Hello! Rendering")
                        1
                    }
                    executes(test())
                }
            }

        }


    }

    fun test() = Command<ServerCommandSource> {
        try {

            TestMsg(
                Items.BUCKET
            ).sendToClient(it.source.player)

        } catch (e: Exception) {
            //e.printStackTrace()
            //Kambrik.Logger.catching(e)
            Kambrik.Logger.error(e)
            e.stackTrace.forEach { element ->
                Kambrik.Logger.error(element)
            }
        }

        1
    }

    private fun dump() = Command<ServerCommandSource> {

        val what = getIdentifier(it, "dump_what")

        if (Registry.REGISTRIES.containsId(what)) {
            val reg = Registry.REGISTRIES[what]!!
            Kambrik.Logger.info("Contents of registry '$what':")
            reg.ids.forEach { id ->
                Kambrik.Logger.info(id)
            }
            it.source.sendFeedback(LiteralText("Dumped contents of '$what' to log."), false)
        } else {
            it.source.sendError(LiteralText("There is no registry with that name."))
        }

        1
    }

}