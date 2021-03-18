package io.ejekta.kambrik

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

object KambrikMod : ModInitializer {

    const val ID = "kambrik"

    override fun onInitialize() {
        Kambrik.Logger.info("Kambrik Says Hello!")

        FabricLoader.getInstance().getEntrypointContainers(ID, KambrikMarker::class.java).forEach {
            Kambrik.Logger.debug("Got mod entrypoint: $it, ${it.entrypoint}, will do Kambrik init here")
            Kambrik.Logger.debug("It came from: ${it.provider.metadata.id}")
            KambrikRegistrar.doRegistrationFor(it)
        }

    }

}