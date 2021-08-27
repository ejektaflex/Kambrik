package io.ejekta.kambrikx.input

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class KambrikInputApi internal constructor() {

    private fun registerBinding(
        key: Int,
        keyTranslation: String,
        keyCategory: String,
        type: InputUtil.Type,
        onPressed: () -> Unit
    ) {
        val bind = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
            keyTranslation,
            type,
            key,
            keyCategory
        )
        )

        ClientTickEvents.END_CLIENT_TICK.register {
            if (bind.wasPressed()) {
                onPressed()
            }
        }

    }

    fun registerKeyboardBinding(
        key: Int = GLFW.GLFW_KEY_UNKNOWN,
        keyTranslation: String,
        keyCategory: String,
        onPressed: () -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.KEYSYM, onPressed)

    fun registerMouseBinding(
        key: Int = GLFW.GLFW_KEY_UNKNOWN,
        keyTranslation: String,
        keyCategory: String,
        onPressed: () -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.MOUSE, onPressed)


}