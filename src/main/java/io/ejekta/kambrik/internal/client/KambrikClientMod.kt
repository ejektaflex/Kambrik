package io.ejekta.kambrik.internal.client

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.KambrikMod
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrikx.data.KambrikPersistence
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader

internal object KambrikClientMod : ClientModInitializer {

    override fun onInitializeClient() {
        // Client data lifecycle management
        ClientLifecycleEvents.CLIENT_STOPPING.register {
            KambrikPersistence.saveAllConfigResults()
        }

        FabricLoader.getInstance().getEntrypointContainers(KambrikMod.ID, KambrikMarker::class.java).forEach {
            KambrikMod.Logger.debug("Got mod entrypoint: $it, ${it.entrypoint} from ${it.provider.metadata.id}, will do Kambrik client init here")
            KambrikRegistrar.doClientRegistrationFor(it)
        }
    }
}


