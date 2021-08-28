package io.ejekta.kambrik.input

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class KambrikInputApi internal constructor() {

    private fun registerBinding(
        key: Int,
        keyTranslation: String,
        keyCategory: String,
        type: InputUtil.Type,
        realTime: Boolean = false,
        bindingDsl: KambrikKeybind.() -> Unit
    ): KambrikKeybind {
        val kambrikKeybind = KambrikKeybind(keyTranslation, type, key, keyCategory, realTime).apply(bindingDsl)
        KeyBindingHelper.registerKeyBinding(
            kambrikKeybind
        )
        return kambrikKeybind
    }

    fun registerKeyboardBinding(
        key: Int = GLFW.GLFW_KEY_UNKNOWN,
        keyTranslation: String,
        keyCategory: String,
        realTime: Boolean = false,
        keybindDsl: KambrikKeybind.() -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.KEYSYM, realTime, keybindDsl)

    fun registerMouseBinding(
        key: Int = GLFW.GLFW_KEY_UNKNOWN,
        keyTranslation: String,
        keyCategory: String,
        realTime: Boolean = false,
        keybindDsl: KambrikKeybind.() -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.MOUSE, realTime, keybindDsl)

}