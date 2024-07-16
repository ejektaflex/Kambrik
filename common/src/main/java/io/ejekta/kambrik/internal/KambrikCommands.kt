package io.ejekta.kambrik.internal

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.command.*
import io.ejekta.kambrik.text.sendError
import io.ejekta.kambrik.text.sendFeedback
import io.ejekta.percale.reverse.toKotlinJsonSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.item.ItemStack
import net.minecraft.network.codec.PacketCodecs.codec
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryOps
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
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
                        Kambrik.Logger.debug("Sending Net Test Message..")
                        TestMsg("net send here").sendToClient(source.playerOrThrow)
                    } catch (e: Exception) {
                        Kambrik.Logger.debug("Kambrik Net Test Message Failed.")
                        e.printStackTrace()
                    }
                }

                "hand" runs {
                    handTests(this)
                }

                "item" runs {
                    itemTests(this, registryAccess)
                }

                "comp" runs {
                    compTests(this)
                }
            }
        }


    }

    fun compTests(commandContext: CommandContext<ServerCommandSource>) {



    }

    fun itemTests(commandContext: CommandContext<ServerCommandSource>, registryAccess: CommandRegistryAccess) {
        commandContext.run {

            val player = source.playerOrThrow
            val held = player.mainHandStack

            val json = Json {
                serializersModule = SerializersModule {
                    codec(Identifier.CODEC)
                }
                prettyPrint = true
            }

            val itemCodec = ItemStack.CODEC

            try {

                val encoded = itemCodec.encodeStart(RegistryOps.of(JsonOps.INSTANCE, source.server.registryManager), held)
                println(encoded)

                for (comp in held.componentChanges.entrySet()) {
                    println("COMP:")
                    println(comp.key)
                    comp.key.codec
                    println(comp.value)
                    val codec = comp.key.codec as Codec<Any>

                    val compId = comp.key.toString() // ick but whatever

                    val opsToUse = when (compId) {
                        "minecraft:enchantments", "minecraft:tool" -> RegistryOps.of(JsonOps.INSTANCE, source.server.registryManager)
                        else -> JsonOps.INSTANCE
                    }

                    val enc = codec.encodeStart(opsToUse, comp.value.get())
                    println(enc)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun handTests(commandContext: CommandContext<ServerCommandSource>) {
        commandContext.run {
            val player = source.playerOrThrow
            val held = player.mainHandStack

            val json = Json {
                serializersModule = SerializersModule {
                    codec(Identifier.CODEC)
                }
                prettyPrint = true
            }


            for (heldEntry in held.components) {
                if (heldEntry.type.codec != null) {
                    try {
                        println("CODEC -> JSON")
                        println(heldEntry)
                        val ks = heldEntry.type.codec?.toKotlinJsonSerializer() as KSerializer<Any>
                        println(ks)

                        println("Trying JSON encode..")
                        val doots = json.encodeToString(ks, heldEntry.value)
                        println("JSON encode success!")
                        println(doots)

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