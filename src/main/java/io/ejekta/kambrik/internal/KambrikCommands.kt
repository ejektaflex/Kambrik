package io.ejekta.kambrik.internal

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.command.KambrikArgBuilder
import io.ejekta.kambrik.command.addCommand
import io.ejekta.kambrik.command.suggestionList
import io.ejekta.kambrik.logging.KambrikMarkers
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kambrik.internal.testing.TestMsg
import io.ejekta.kambrik.text.textLiteral
//import io.ejekta.kambrik.internal.testing.TestMsg
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.argument.IdentifierArgumentType.getIdentifier
import net.minecraft.item.Items
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.tag.*
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

internal object KambrikCommands : CommandRegistrationCallback {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean) {

        dispatcher.addCommand(KambrikMod.ID) {
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
                "registry" {
                    val dumpables = suggestionList { Registry.REGISTRIES.toList().map { it.key.value.toString() } }
                    argIdentifier("dump_what", items = dumpables) runs dumpRegistry()
                }

                "tags" {
                    "blocks" { tagQueryDump({ BlockTags.getTagGroup().tags }) { it.identifier } }
                    "entity_types" { tagQueryDump({ EntityTypeTags.getTagGroup().tags }) { it.identifier } }
                    "game_events" { tagQueryDump({ GameEventTags.getTagGroup().tags }) { it.id } }
                    "fluids" { tagQueryDump({ FluidTags.getTagGroup().tags }) { it.identifier } }
                    "items" { tagQueryDump({ ItemTags.getTagGroup().tags }) { it.identifier } }
                }
            }

            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                "test" {
                    "output" runs {
                        Kambrik.Logger.info(KambrikMarkers.General, "Hello! General")
                        Kambrik.Logger.info(KambrikMarkers.Rendering, "Hello! Rendering")
                        1
                    }
                    this runs test()
                    "text" runs text()
                }
            }

        }


    }

    private fun <T, R : Comparable<R>> KambrikArgBuilder<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>>.tagQueryDump(
        inTags: () -> Map<Identifier, Tag<T>>,
        idGetter: (T) -> R
    ) {
        executes {
            dumpTagListing(inTags(), idGetter).run(it)
        }
        argIdentifier("tag_id", items = suggestionList { inTags().keys.toList() }) {
            executes {
                val computedTags = inTags()
                var results = 0
                val queryId = getIdentifier(it, "tag_id")
                val foundItem = computedTags.keys.find { id -> id == queryId }
                if (foundItem == null) {
                    it.source.sendError(LiteralText("Tag does not exist."))
                } else {
                    dumpTagListing(computedTags.filter { entry -> entry.key == queryId }, idGetter).run(it)
                    results = computedTags.size
                }

                results
            }
        }
    }

    private fun <T, R : Comparable<R>> dumpTagListing(inTags: Map<Identifier, Tag<T>>, idGetter: (T) -> R) = Command<ServerCommandSource> {
        for ((id, tag) in inTags.toSortedMap { a, b -> a.compareTo(b) }) {
            Kambrik.Logger.info("[TAG] $id")
            for (item in tag.values().sortedBy(idGetter)) {
                Kambrik.Logger.info("  * [ID] ${idGetter(item)}")
            }
        }
        1
    }


    private fun dumpRegistry() = Command<ServerCommandSource> {

        val what = getIdentifier(it, "dump_what")

        if (Registry.REGISTRIES.containsId(what)) {
            val reg = Registry.REGISTRIES[what]!!
            Kambrik.Logger.info("Contents of registry '$what':")
            reg.ids.forEach { id ->
                Kambrik.Logger.info("  * [ID] $id")
            }
            it.source.sendFeedback(LiteralText("Dumped contents of '$what' to log."), false)
        } else {
            it.source.sendError(LiteralText("There is no registry with that name."))
        }

        1
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

    fun text() = Command<ServerCommandSource> {

        val test = textLiteral("Hello World!") {
            onHoverShowText {
                +Formatting.ITALIC
                +textLiteral("How are you?")
            }
        }

        it.source.sendFeedback(test, false)

        println(test)
        println(test.string)

        1
    }

}