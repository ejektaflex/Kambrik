package io.ejekta.kambrik

import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.client.KambrikModForgeClient
import io.ejekta.kambrik.internal.KambrikCommands
import io.ejekta.kambrik.internal.TestMsg
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.forge.runForDist
import java.util.ServiceLoader

@Mod("kambrik")
object KambrikModForge {
    init {
        FORGE_BUS.addListener(this::registerCommands)

        ServiceLoader.load(KambrikSharedApi::class.java).reload()
        println("Service loaders found: ${ServiceLoader.load(KambrikSharedApi::class.java).toList()}")

        try {
            Kambrik.Message.registerClientMessage(
                TestMsg.serializer(),
                TestMsg::class,
                Kambrik.idOf("test_msg")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        runForDist(
            clientTarget = {
                // Register mod event bus
                MOD_CONTEXT.getKEventBus().register(KambrikModForgeClient::class.java)
            },
            serverTarget = {
                MOD_CONTEXT.getKEventBus().register(Kambridge)
            }
        )
    }

    @JvmStatic
    @SubscribeEvent
    fun registerCommands(evt: RegisterCommandsEvent) {
        KambrikCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }

}