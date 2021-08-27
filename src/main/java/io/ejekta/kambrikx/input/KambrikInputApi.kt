package io.ejekta.kambrikx.input

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class KambrikInputApi internal constructor() {

    private fun registerBinding(
        key: Int,
        keyTranslation: String,
        keyCategory: String,
        type: InputUtil.Type,
        bindingDsl: KambrikKeybind.() -> Unit
    ): KambrikKeybind {

        val kambrikKeybind = KambrikKeybind(keyTranslation, type, key, keyCategory).apply(bindingDsl)
        KeyBindingHelper.registerKeyBinding(
            kambrikKeybind
        )

        return kambrikKeybind
    }

    fun registerKeyboardBinding(
        key: Int = GLFW.GLFW_KEY_UNKNOWN,
        keyTranslation: String,
        keyCategory: String,
        keybindDsl: KambrikKeybind.() -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.KEYSYM, keybindDsl)

    fun registerMouseBinding(
        key: Int = GLFW.GLFW_KEY_UNKNOWN,
        keyTranslation: String,
        keyCategory: String,
        keybindDsl: KambrikKeybind.() -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.MOUSE, keybindDsl)




}