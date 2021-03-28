package io.ejekta.kambrik.internal

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.StringArgumentType
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.KambrikMod
import io.ejekta.kambrik.api.command.suggestionList
import io.ejekta.kambrik.api.logging.KambrikMarkers
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.command.ServerCommandSource

object KambrikCommands : CommandRegistrationCallback {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean) {
        Kambrik.Command.addCommand(KambrikMod.ID, dispatcher) {
            "logging" {
                val currMarkers = suggestionList { KambrikMarkers.Registry.map { "\"" + it.key + "\"" }.sorted() }

                "markers" {
                    stringArg("marker", items = currMarkers) {
                        "set" {
                            boolArg("enabled") runs {
                                val marker = StringArgumentType.getString(it, "marker")
                                val enabled = getBool(it, "enabled")
                                KambrikMod.config.edit {
                                    if (enabled == KambrikMarkers.Registry[marker]) {
                                        markers.remove(marker)
                                    } else {
                                        markers[marker] = enabled
                                    }
                                }
                                KambrikMod.configureLoggerFilters()
                                1
                            }
                        }
                    }
                }

            }

            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                "test" {
                    "output" runs {
                        Kambrik.Logger.info(KambrikMarkers.General, "Hello! General")
                        Kambrik.Logger.info(KambrikMarkers.Rendering, "Hello! Rendering")
                        1
                    }
                }
            }

        }
    }
}