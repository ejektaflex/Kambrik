package io.ejekta.kambrik.input

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

sealed class KambrikModifiedBind(val keyMod: KambrikKeyModifier = KambrikKeyModifier.EMPTY) {

    abstract fun getIsPressed(): Boolean

    class Key(val keyCode: InputUtil.Key, mod: KambrikKeyModifier = KambrikKeyModifier.EMPTY) : KambrikModifiedBind(mod) {
        override fun getIsPressed(): Boolean {
            return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, keyCode.code)
        }
    }

    class Mouse(val key: Int, mod: KambrikKeyModifier = KambrikKeyModifier.EMPTY) : KambrikModifiedBind(mod) {
        override fun getIsPressed(): Boolean {
            return GLFW.glfwGetMouseButton(
                MinecraftClient.getInstance().window.handle,
                key
            ) == 1
        }
    }
}