package io.ejekta.kambrik.input

import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

class KambrikKeyModifier(
    val shift: Boolean = false,
    val ctrl: Boolean = false,
    val alt: Boolean = false
) {
    fun getIsPressed(): Boolean {
        val mc = Minecraft.getInstance()
        return (shift == mc.hasShiftDown() && ctrl == mc.hasControlDown() && alt == mc.hasAltDown())
    }

    companion object {
        val EMPTY = KambrikKeyModifier()
    }
}