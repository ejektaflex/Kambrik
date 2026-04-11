package io.ejekta.kambrik.fabric.internal.client

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.fabric.bridge.KambrikSharedApiFabric
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.kambrikx.data.KambrikPersistence
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

object KambrikClientMod : ClientModInitializer {

    override fun onInitializeClient() {
        KambrikSharedApiFabric.clientMessageRegistrar = { id ->
            ClientPlayNetworking.registerGlobalReceiver(id) { payload, context ->
                (payload as KambrikMsg).onClientReceived()
            }
        }
        KambrikSharedApiFabric.clientMessageSender = { msg ->
            ClientPlayNetworking.send(msg)
        }

        // Client data lifecycle management

        WorldRenderEvents.LAST.register(WorldRenderEvents.Last {
            Kambrik.Input.updateRealBinds()
        })

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            Kambrik.Input.updateNormBinds()
        })

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            KambrikPersistence.saveAllConfigResults()
        }

    }

}

