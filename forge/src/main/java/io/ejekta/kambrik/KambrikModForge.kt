package io.ejekta.kambrik

import io.ejekta.kambrik.bridge.LoaderApiForge
import io.ejekta.kambrik.bridge.LoaderBridge
import io.ejekta.kambrik.internal.KambrikCommands
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod("kambrik")
object KambrikModForge {
    init {
        LoaderBridge.setupApi(LoaderApiForge())
        //MOD_BUS.addListener<RegisterCommandsEvent>(this::registerCommands)
        FORGE_BUS.addListener(this::registerCommands)
    }

    @JvmStatic
    @SubscribeEvent
    fun registerCommands(evt: RegisterCommandsEvent) {
        println("Forge evt bus registering Kambrik commands")
        KambrikCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }


}