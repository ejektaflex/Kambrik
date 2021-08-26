package io.ejekta.kambrikx.keybind

import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class KeybindBuilder {
    var key: Int = GLFW.GLFW_KEY_UNKNOWN
    lateinit var mode: Mode
    lateinit var translation: String
    lateinit var category: String
    lateinit var executor: () -> Unit
        private set

    fun execute(lambda: () -> Unit) {
        executor = lambda
    }

    enum class Mode(val type: InputUtil.Type) {
        KEYBOARD(InputUtil.Type.KEYSYM),
        MOUSE(InputUtil.Type.MOUSE)
    }
}