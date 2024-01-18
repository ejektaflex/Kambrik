package io.ejekta.kambrik.neoforge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.TestJava
import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.neoforge.client.KambrikModForgeClient
import io.ejekta.kambrik.internal.KambrikCommands
import io.ejekta.kambrik.internal.TestMsg
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.RegisterCommandsEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import java.util.ServiceLoader

@Mod("kambrik")
object KambrikModForge {
    init {
        FORGE_BUS.addListener(this::registerCommands)


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