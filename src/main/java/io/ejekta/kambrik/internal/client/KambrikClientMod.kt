package io.ejekta.kambrik.internal.client

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.data.config.ConfigDataRegistrar
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import org.lwjgl.glfw.GLFW

internal object KambrikClientMod : ClientModInitializer {

    override fun onInitializeClient() {

        // Client data lifecycle management

        ClientLifecycleEvents.CLIENT_STOPPING.register(ClientLifecycleEvents.ClientStopping {
            ConfigDataRegistrar.saveAllResults()
        })

    }

}


