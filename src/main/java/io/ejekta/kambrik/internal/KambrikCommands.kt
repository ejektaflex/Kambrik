package io.ejekta.kambrik.internal

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.KambrikMod
import io.ejekta.kambrik.api.command.suggestionList
import io.ejekta.kambrik.api.logging.KambrikMarkers
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import io.ejekta.kambrikx.api.serial.serializers.CompoundTagSerializer
import io.ejekta.kambrikx.api.serial.serializers.Holder
import io.ejekta.kambrikx.api.serial.serializers.TagSerializerTwo
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.argument.IdentifierArgumentType.getIdentifier
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.registry.Registry

object KambrikCommands : CommandRegistrationCallback {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean) {
        Kambrik.Command.addCommand(KambrikMod.ID, dispatcher) {
            "logging" {
                val currMarkers = suggestionList { KambrikMarkers.Registry.map { "\"" + it.key + "\"" }.sorted() }

                "markers" {
                    stringArg("marker", items = currMarkers) {
                        "set" {
                            boolArg("enabled") runs {
                                val marker = getString(it, "marker")
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

            "dump" {
                val dumpables = suggestionList { Registry.REGISTRIES.toList().map { it.key.value.toString() } }
                identifierArg("dump_what", items = dumpables) runs dump()
            }

            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                "test" {
                    "output" runs {
                        Kambrik.Logger.info(KambrikMarkers.General, "Hello! General")
                        Kambrik.Logger.info(KambrikMarkers.Rendering, "Hello! Rendering")
                        1
                    }
                    executes {
                        test(it)
                        1
                    }
                }
            }

        }
    }

    fun test(ctx: CommandContext<ServerCommandSource>): Int {
        try {
            val t = CompoundTag().apply {
                putString("Hai", "There")
                putByte("Yo", 1)
                putByte("Ma", 3)
            }

            val u = Holder(t)

            //val result = NbtFormat.Default.encodeToTag(CompoundTagSerializer(), t)
            val result = NbtFormat.Default.encodeToTag(Holder.serializer(), u)


            ctx.source.sendFeedback(LiteralText(result.toString()), false)

            val result2 = NbtFormat.Default.decodeFromTag(Holder.serializer(), result)

            ctx.source.sendFeedback(LiteralText(result2.toString()), false)

        } catch (e: Exception) {
            //e.printStackTrace()
            //Kambrik.Logger.catching(e)
            Kambrik.Logger.error(e)
            e.stackTrace.forEach { element ->
                Kambrik.Logger.error(element)
            }
        }

        return 1
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