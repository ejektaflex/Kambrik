package io.ejekta.kambrik.fabric

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.internal.KambrikCommands
import io.ejekta.kambrik.internal.TestMsg
import io.ejekta.kambrikx.data.KambrikPersistence
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader


class KambrikModFabric : ModInitializer {

    override fun onInitialize() {

        // Kambrik commands
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback(KambrikCommands::register))

        // Server data lifecycle management

        Kambridge.registerTestMessage()

        ServerLifecycleEvents.SERVER_STARTED.register {
            KambrikPersistence.loadServerResults()
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            KambrikPersistence.saveAllServerResults()
            if (FabricLoader.getInstance().environmentType != EnvType.CLIENT) {
                KambrikPersistence.saveAllConfigResults()
            }
        }

    }
}

