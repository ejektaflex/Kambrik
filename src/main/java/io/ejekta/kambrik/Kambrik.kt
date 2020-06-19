package io.ejekta.kambrik

import net.fabricmc.api.ModInitializer

object Kambrik : ModInitializer {
    internal const val ID = "kambrik"

    override fun onInitialize() {
        println("Common init")
    }
}