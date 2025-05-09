package io.ejekta.kambrik.internal

import com.google.gson.JsonSerializer
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.command.*
import io.ejekta.kambrik.ext.Identifier
import io.ejekta.kambrik.text.sendSuccess
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.percale.Percale
import io.ejekta.percale.contextualCodec
import io.ejekta.percale.deserialize
import io.ejekta.percale.reverse.GsonElementSerializer
import io.ejekta.percale.reverse.GsonObjectSerializer
import io.ejekta.percale.reverse.PercaleJson
import io.ejekta.percale.reverse.toSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

object KambrikCommands {
    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        buildContext: CommandBuildContext,
        selection: Commands.CommandSelection
    ) {

        dispatcher.addCommand(Kambrik.ID) {

            "dump" {
                "registry" {
                    val dumpables = suggestionList { BuiltInRegistries.REGISTRY.entrySet().toList().map { it.key.location() } }
                    argResource("dump_what", items = dumpables) runs { what ->
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
                        Kambrik.Logger.debug("Sending Net Test Message..")
                        TestMsg("net send here", Identifier("a", "b")).sendToClient(source.playerOrException)
                    } catch (e: Exception) {
                        Kambrik.Logger.debug("Kambrik Net Test Message Failed.")
                        e.printStackTrace()
                    }
                }

                "hand" runs {
                    handTests(this)
                }

                "item" runs {
                    itemTests(this, buildContext)
                }

                "comp" runs {
                    compTests(this)
                }

                "percale" {
                    "toggle" {
                        "logs" runs {
                            Percale.shouldSyslog = !Percale.shouldSyslog
                            println("Percale logging is now: ${Percale.shouldSyslog}")
                        }
                    }
                }
            }
        }


    }

    fun compTests(commandContext: CommandContext<CommandSourceStack>) {



    }

    fun itemTests(commandContext: CommandContext<CommandSourceStack>, registryAccess: CommandBuildContext) {
        commandContext.run {

            try {

                val player = source.playerOrException
                val held = player.mainHandItem

                val json = Json {
                    serializersModule = SerializersModule {
                        contextualCodec(ResourceLocation.CODEC)
                        contextual(GsonElementSerializer)
                        contextual(GsonObjectSerializer)
                    }
                    prettyPrint = true
                }

                val itemCodec = ItemStack.CODEC

                val percaleFormat = PercaleJson(RegistryOps.create(JsonOps.INSTANCE, source.server.registryAccess()), json)

                val itemString = percaleFormat.dynamicEncodeToString(held, itemCodec.toSerializer())

                println(itemString)

//                val encoded = itemCodec.encodeStart(RegistryOps.of(JsonOps.INSTANCE, source.server.registryManager), held)
//                println(encoded)

                for (comp in held.componentsPatch.entrySet()) {
                    println("COMP:")
                    println(comp.key)
                    comp.key.codec()
                    val cv = comp.value.getOrNull()
                    println(cv)
                    val codec = comp.key.codec() as Codec<Any>
                    println("CODEC: $codec")
                    val codSer = codec.toSerializer()
                    println("CODESER: $codSer")

                    try {
                        if (cv != null) {
                            println("GEN SERD..")
                            val serd = percaleFormat.dynamicEncodeToString(cv, codSer)
                            println("SERD: $serd")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun handTests(commandContext: CommandContext<CommandSourceStack>) {
        commandContext.run {

            try {
                println("Doing hand test")

                val player = source.playerOrException
                val held = player.mainHandItem

                val json = Json {
                    serializersModule = SerializersModule {
                        contextualCodec(ResourceLocation.CODEC)
                        contextualCodec(ItemStack.CODEC)
                        contextual(GsonElementSerializer)
                        contextual(GsonObjectSerializer)
                    }
                    prettyPrint = true
                }

                val regOps = RegistryOps.create(JsonOps.INSTANCE, source.server.registryAccess())

                val percaleFormat = PercaleJson(regOps, json)

                val itemCodec = ItemStack.CODEC

                val itemSer = itemCodec.toSerializer(JsonObject.serializer())

                println("Item Ser is: $itemSer - ${itemSer.descriptor} - ${itemSer.descriptor.kind}")


                val jsony = """
                {
                "id": "minecraft:stick",
                "count": 1,
                "components": {
                    "minecraft:damage": 2,
                    "minecraft:custom_name": "\"Sword of Love\"",
                    "minecraft:repair_cost": 1,
                    "minecraft:enchantments": {
                        "levels": {
                            "minecraft:sharpness": 5
                        }
                    }
                }
                }
                """.trimIndent()

                val comps = percaleFormat.dynamicDecodeFromString(jsony, itemSer)

                println("COMPS:")
                println(comps)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun dumpRegistry(what: ResourceLocation) = kambrikServerCommand {
        if (BuiltInRegistries.REGISTRY.containsKey(what)) {
            val reg = BuiltInRegistries.REGISTRY[what]!!
            Kambrik.Logger.info("Contents of registry '$what':")
            reg.keySet().forEach { id ->
                Kambrik.Logger.info("  * [ID] $id")
            }
            source.sendSuccess("Dumped contents of '$what' to log.")
        } else {
            source.sendFailure(textLiteral("There is no registry with that name."))
        }
    }

    private fun text() = kambrikServerCommand {
        val test = Component.literal("Hello World!")
        source.sendSystemMessage(test)
    }

}