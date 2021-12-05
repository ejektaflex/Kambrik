package io.ejekta.kambrik.internal.server

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.KambrikMod
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.loader.api.FabricLoader

object KambrikServerMod : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        FabricLoader.getInstance().getEntrypointContainers(KambrikMod.ID, KambrikMarker::class.java).forEach {
            KambrikMod.Logger.debug("Got mod entrypoint: $it, ${it.entrypoint} from ${it.provider.metadata.id}, will do Kambrik server init here")
            KambrikRegistrar.doServerRegistrationFor(it)
        }
    }
}