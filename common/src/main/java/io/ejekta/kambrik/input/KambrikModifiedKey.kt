package io.ejekta.kambrik.input

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

sealed class KambrikModifiedBind(val keyMod: KambrikKeyModifier = KambrikKeyModifier.EMPTY) {

    abstract fun getIsPressed(): Boolean

    class Key(val keyCode: InputConstants.Key, mod: KambrikKeyModifier = KambrikKeyModifier.EMPTY) : KambrikModifiedBind(mod) {
        override fun getIsPressed(): Boolean {
            return InputConstants.isKeyDown(Minecraft.getInstance().window.window, keyCode.value) && keyMod.getIsPressed()
        }
    }

    class Mouse(val key: Int, mod: KambrikKeyModifier = KambrikKeyModifier.EMPTY) : KambrikModifiedBind(mod) {
        override fun getIsPressed(): Boolean {
            return GLFW.glfwGetMouseButton(
                Minecraft.getInstance().window.window,
                key
            ) == 1 && keyMod.getIsPressed()
        }
    }
}