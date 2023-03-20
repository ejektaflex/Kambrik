package io.ejekta.kambrik.input

import io.ejekta.kambrik.ext.client.getBoundKey
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

data class KambrikKeybind(
    val translation: String,
    val type: InputUtil.Type,
    val modKey: ModifiedKeyCode,
    val keyCategory: String, // TODO we already have a category var?
    val realTime: Boolean = false
) : KeyBinding(translation, type, modKey.keycode, keyCategory) {


    override fun setBoundKey(boundKey: InputUtil.Key?) {
        super.setBoundKey(boundKey)
        val heldCodes = getAllHeldKeyCodes().toMutableList()
        boundKey?.let {
            heldCodes -= boundKey.code
        }
        modKey.modifiers = heldCodes
    }

    data class ModifiedKeyCode(var keycode: Int, var modifiers: List<Int>) {
        fun areModifiersAllPressed(): Boolean {
            return modifiers.all {
                InputUtil.isKeyPressed(
                    MinecraftClient.getInstance().window.handle, it
                )
            }
        }
    }

    private fun getAllHeldKeyCodes(): List<Int> {
        val allKeyNums = InputUtil.Type.KEYSYM.map.keys
        val pressedList = allKeyNums.filter {
            InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, it)
        }
        return pressedList
    }

    val moddedText: Text
        get() {
            val mKeys = (modKey.modifiers.map {
                InputUtil.Type.KEYSYM.createFromCode(it)
            } + listOf(getBoundKey())).map {
                when (it.code) {
                    GLFW.GLFW_KEY_LEFT_SHIFT -> Text.literal("LShft")
                    GLFW.GLFW_KEY_RIGHT_SHIFT -> Text.literal("RShft")
                    GLFW.GLFW_KEY_LEFT_CONTROL -> Text.literal("LCtrl")
                    GLFW.GLFW_KEY_RIGHT_CONTROL -> Text.literal("RCtrl")
                    GLFW.GLFW_KEY_LEFT_ALT -> Text.literal("LAlt")
                    GLFW.GLFW_KEY_RIGHT_ALT -> Text.literal("RAlt")
                    else -> it.localizedText
                }
            }.reduce { a, b ->
                a.copy().append(Text.literal(" + ").append(b.copy()))
            }
            return mKeys
        }

    private var keyDown = {}

    private var keyUp = {}

    private var keyRepeat = {}

    fun onDown(func: () -> Unit) {
        keyDown = func
    }

    fun onUp(func: () -> Unit) {
        keyUp = func
    }

    fun onRepeat(func: () -> Unit) {
        keyRepeat = func
    }

    var isDown = false
        private set

    override fun getTranslationKey(): String {
        return super.getTranslationKey()
    }

    fun actualTranslation(): Text {
        return Text.literal("Hello!")
    }

    override fun getBoundKeyLocalizedText(): Text {
        return moddedText
    }

    // Only considered pressed when all modifiers are also pressed!
    override fun setPressed(pressed: Boolean) {
        if (pressed && modKey.areModifiersAllPressed()) {
            if (isPressed) { // If already pressed down, this must be a key repeat
                keyRepeat()
            }
            super.setPressed(true)
        } else {
            super.setPressed(false)
        }
    }



    private fun update(wasPressed: Boolean) {
        if (!isDown && wasPressed) { // Capture first press
            isDown = true
            keyDown()
        } else if (isDown && !wasPressed) { // Capture first 'un-press'
            isDown = false
            keyUp()
            isPressed = false
        }
    }

    init {
        if (realTime) {
            WorldRenderEvents.LAST.register(WorldRenderEvents.Last {
                update(isPressed)
            })
        } else {
            ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
                update(isPressed)
            })
        }
    }

}