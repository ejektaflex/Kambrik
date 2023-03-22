package io.ejekta.kambrik.internal.client

import io.ejekta.kambrikx.data.KambrikPersistence
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents

internal object KambrikClientMod : ClientModInitializer {

    override fun onInitializeClient() {

        // Client data lifecycle management

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            KambrikPersistence.saveAllConfigResults()
        }

    }

}


