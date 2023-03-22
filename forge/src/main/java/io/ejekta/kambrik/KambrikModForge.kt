package io.ejekta.kambrik

import io.ejekta.kambrik.bridge.LoaderApiForge
import io.ejekta.kambrik.bridge.LoaderBridge
import io.ejekta.kambrik.client.KambrikModForgeClient
import io.ejekta.kambrik.internal.KambrikCommands
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod("kambrik")
object KambrikModForge {
    init {
        LoaderBridge.setupApi(LoaderApiForge())
        FORGE_BUS.addListener(this::registerCommands)

        runForDist(
            clientTarget = {
                println("Registering client listeners..")

                // Register mod event bus
                MOD_CONTEXT.getKEventBus().register(KambrikModForgeClient::class.java)

            },
            serverTarget = {

            }
        )

    }

    @JvmStatic
    @SubscribeEvent
    fun registerCommands(evt: RegisterCommandsEvent) {
        println("Forge evt bus registering Kambrik commands")
        KambrikCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }



}