package io.ejekta.kambrik.internal

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.ejekta.kambrik.command.*
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kambrik.text.sendError
import io.ejekta.kambrik.text.sendFeedback
import io.ejekta.kambrik.text.textLiteral
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
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

    private fun <T, R : Comparable<R>> KambrikArgBuilder<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>>.tagQueryDump(
        inTags: () -> Map<Identifier, Tag<T>>,
        idGetter: (T) -> R
    ) {
        this runs { dumpTagListing(inTags(), idGetter).run(this) }

        argIdentifier("tag_id", items = suggestionList { inTags().keys.toList() }) runs { tagId ->
            val computedTags = inTags()
            val foundItem = computedTags.keys.find { id -> id == tagId() }
            if (foundItem == null) {
                source.sendError("Tag does not exist.")
            } else {
                dumpTagListing(computedTags.filter { entry -> entry.key == tagId() }, idGetter).run(this)
            }
        }

    }

    private fun <T, R : Comparable<R>> dumpTagListing(inTags: Map<Identifier, Tag<T>>, idGetter: (T) -> R) = kambrikServerCommand {
        for ((id, tag) in inTags.toSortedMap { a, b -> a.compareTo(b) }) {
            KambrikMod.Logger.info("[TAG] $id")
            for (item in tag.values().sortedBy(idGetter)) {
                KambrikMod.Logger.info("  * [ID] ${idGetter(item)}")
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