package io.ejekta.kambrik

import io.ejekta.kambrik.bridge.LoaderApi
import io.ejekta.kambrik.input.KambrikKeybind
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.option.KeyBinding

class LoaderApiFabric : LoaderApi {

    override fun registerKeybind(kb: KeyBinding) {
        KeyBindingHelper.registerKeyBinding(kb)
    }

    override fun hookKeybindUpdates(kambrikKeybind: KambrikKeybind, func: KambrikKeybind.() -> Unit) {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            kambrikKeybind.func()
        })
    }

    override fun hookKeybindUpdatesRealtime(kambrikKeybind: KambrikKeybind, func: KambrikKeybind.() -> Unit) {
        WorldRenderEvents.LAST.register(WorldRenderEvents.Last {
            kambrikKeybind.func()
        })
    }

}