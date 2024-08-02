package io.ejekta.kambrik.input

import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW

class KambrikKeyModifier(
    val shift: Boolean = false,
    val ctrl: Boolean = false,
    val alt: Boolean = false
) {
    fun getIsPressed(): Boolean {
        return (shift == Screen.hasShiftDown() && ctrl == Screen.hasControlDown() && alt == Screen.hasAltDown())
    }

    companion object {
        val EMPTY = KambrikKeyModifier()
    }
}