package io.ejekta.kambrik

import io.ejekta.kambrik.bridge.LoaderApiFabric
import io.ejekta.kambrik.bridge.LoaderBridge
import net.fabricmc.api.ModInitializer


class KambrikModFabric : ModInitializer {

    init {
        LoaderBridge.setupApi(LoaderApiFabric())
    }

    override fun onInitialize() {
        println("Kambrik init!")
    }
}

