package io.ejekta.kambrik.input

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class KambrikInputApi internal constructor() {

    fun registerBinding(
        key: Int,
        keyTranslation: String,
        keyCategory: String,
        type: InputUtil.Type,
        modifiers: List<Int>? = null,
        realTime: Boolean = false,
        register: Boolean = true,
        bindingDsl: (KambrikKeybind.() -> Unit)? = null
    ): KambrikKeybind {
        // Create modified key from params
        val keyWithMods = when {
            modifiers?.isNotEmpty() == true -> KambrikKeybind.ModifiedKeyCode(key, modifiers)
            else -> KambrikKeybind.ModifiedKeyCode(key, emptyList())
        }
        // Create Kambrik keybind
        val kambrikKeybind = KambrikKeybind(keyTranslation, type, keyWithMods, keyCategory, realTime).apply {
            bindingDsl?.let { it() }
        }

        if (register) {
            // Register keybind
            KeyBindingHelper.registerKeyBinding(
                kambrikKeybind
            )
        }

        return kambrikKeybind
    }

    /**
     * Registers a new Kambrik Keyboard Keybind.
     * @param key Which key to register with. See GLFW keys for reference
     * @param keyTranslation The translation key for this keybind's localization
     * @param keyCategory Which category in the Controls menu this key belongs to
     * @param realTime Whether key updates are processed per tick or per frame (default: false or per tick)
     * @param keybindDsl A DSL where you can specify how the keybind reacts to onUp and onDown presses
     */
    fun registerKeyboardBinding(
        key: Int = GLFW.GLFW_KEY_UNKNOWN,
        keyTranslation: String,
        keyCategory: String,
        modifiers: List<Int>? = null,
        realTime: Boolean = false,
        register: Boolean = true,
        keybindDsl: (KambrikKeybind.() -> Unit)? = null
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.KEYSYM, modifiers, realTime, register, keybindDsl)

    /**
     * Registers a new Kambrik Mouse Keybind.
     * @param key Which mouse button to register with. See GLFW keys for reference
     * @param keyTranslation The translation key for this keybind's localization
     * @param keyCategory Which category in the Controls menu this key belongs to
     * @param realTime Whether key updates are processed per tick or per frame (default: false or per tick)
     * @param keybindDsl A DSL where you can specify how the keybind reacts to onUp and onDown presses
     */
    fun registerMouseBinding(
        key: Int = GLFW.GLFW_KEY_UNKNOWN,
        keyTranslation: String,
        keyCategory: String,
        modifiers: List<Int>? = null,
        realTime: Boolean = false,
        register: Boolean = true,
        keybindDsl: KambrikKeybind.() -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.MOUSE, modifiers, realTime, register, keybindDsl)

}