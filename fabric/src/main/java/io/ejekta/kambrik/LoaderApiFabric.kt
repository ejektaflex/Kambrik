package io.ejekta.kambrik

import io.ejekta.kambrik.bridge.LoaderApi
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding

class LoaderApiFabric : LoaderApi {

    override fun registerKeybind(kb: KeyBinding) {
        KeyBindingHelper.registerKeyBinding(kb)
    }

}