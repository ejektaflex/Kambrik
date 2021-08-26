package io.ejekta.kambrikx.input.keybind

import io.ejekta.kambrik.internal.KambrikExperimental
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding

@KambrikExperimental
class KambrikKeybindingApi internal constructor() {
    fun registerKeybinding(lambda: KeybindBuilder.() -> Unit): KeyBinding {
        val build = KeybindBuilder().apply(lambda)
        val bind = KeyBindingHelper.registerKeyBinding(KeyBinding(
            build.translation,
            build.mode.type,
            build.key,
            build.category
        ))

        ClientTickEvents.END_CLIENT_TICK.register {
            if (bind.wasPressed()) build.executor()
        }

        return bind
    }
}