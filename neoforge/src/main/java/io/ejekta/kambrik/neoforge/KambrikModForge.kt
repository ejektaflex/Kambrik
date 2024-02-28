package io.ejekta.kambrik.neoforge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.internal.KambrikCommands
import io.ejekta.kambrik.neoforge.bridge.KambrikSharedApiForge
import io.ejekta.kambrik.neoforge.client.KambrikModForgeClient
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod("kambrik")
object KambrikModForge {
    init {

        try {
            Kambridge.registerTestMessage()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        FORGE_BUS.addListener(KambrikForgeEvents::registerCommands)

        MOD_CONTEXT.getKEventBus().register(KambrikModCommonEvents::class.java)

        runForDist(
            clientTarget = {
                // Register mod event bus
                MOD_CONTEXT.getKEventBus().register(KambrikModForgeClient::class.java)
            },
            serverTarget = { }
        )
    }

    object KambrikForgeEvents {
        @JvmStatic
        @SubscribeEvent
        fun registerCommands(evt: RegisterCommandsEvent) {
            KambrikCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
        }
    }

    object KambrikModCommonEvents {
        @JvmStatic
        @SubscribeEvent
        fun registerPayloads(event: RegisterPayloadHandlerEvent) {
            Kambrik.Logger.info("Registering network payloads..")
            (Kambridge as KambrikSharedApiForge).registerPayloads(event)
        }
    }

}