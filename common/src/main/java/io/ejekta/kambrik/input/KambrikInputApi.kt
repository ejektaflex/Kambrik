package io.ejekta.kambrik.input

import io.ejekta.kambrik.bridge.Kambridge
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
        Kambridge.registerKeybind(kambrikKeybind)
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
        realTime: Boolean = false,
        keybindDsl: KambrikKeybind.() -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.KEYSYM, realTime, keybindDsl)

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
        realTime: Boolean = false,
        keybindDsl: KambrikKeybind.() -> Unit
    ) = registerBinding(key, keyTranslation, keyCategory, InputUtil.Type.MOUSE, realTime, keybindDsl)

}