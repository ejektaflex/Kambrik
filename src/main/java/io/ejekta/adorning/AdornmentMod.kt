package io.ejekta.adorning

import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.api.ModInitializer

object AdornmentMod : ModInitializer, KambrikAutoRegistrar {
    override fun onInitialize() {
        TODO("Not yet implemented")
    }

    val DISPLAY_ITEM_HELMET = "helmet" forItem AdornmentDisplay()
    val DISPLAY_ITEM_CHEST = "chest" forItem AdornmentDisplay()
    val DISPLAY_ITEM_LEGGINGS = "leggings" forItem AdornmentDisplay()
    val DISPLAY_ITEM_FEET = "feet" forItem AdornmentDisplay()

    const val ID = "adornments"
}