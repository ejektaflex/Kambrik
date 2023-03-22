package io.ejekta.kambrik

import io.ejekta.kambrik.bridge.LoaderApiFabric
import io.ejekta.kambrik.bridge.LoaderBridge
import io.ejekta.kambrik.internal.KambrikCommands
import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrikx.data.KambrikPersistence
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader


class KambrikModFabric : ModInitializer {

    init {
        LoaderBridge.setupApi(LoaderApiFabric())
    }

    override fun onInitialize() {
        println("Kambrik init!")

        // Auto Registration feature
        FabricLoader.getInstance().getEntrypointContainers(Kambrik.ID, KambrikMarker::class.java).forEach {
            Kambrik.Logger.debug("Got mod entrypoint: $it, ${it.entrypoint} from ${it.provider.metadata.id}, will do Kambrik init here")
            KambrikRegistrar.doRegistrationFor(it)
        }

        // Kambrik commands
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback(KambrikCommands::register))

        // Server data lifecycle management

        ServerLifecycleEvents.SERVER_STARTED.register {
            KambrikPersistence.loadServerResults()
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            KambrikPersistence.saveAllServerResults()
            if (FabricLoader.getInstance().environmentType != EnvType.CLIENT) {
                KambrikPersistence.saveAllConfigResults()
            }
        }

//        Kambrik.Criterion.addCriterionHandler("""
//            {
//              "trigger": "minecraft:enter_block",
//              "conditions": {
//                "block": "minecraft:rose_bush",
//                "state": {
//                  "half": "lower"
//                }
//              }
//            }
//            """.trimIndent()
//        ) {
//            println("Doot!")
//        }

    }
}

