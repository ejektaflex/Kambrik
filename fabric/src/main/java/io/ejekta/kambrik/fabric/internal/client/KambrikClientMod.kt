package io.ejekta.kambrik.fabric.internal.client

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.TestMsg
import io.ejekta.kambrikx.data.KambrikPersistence
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents

object KambrikClientMod : ClientModInitializer {

    override fun onInitializeClient() {

        // Client data lifecycle management

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            KambrikPersistence.saveAllConfigResults()
        }

    }

}


