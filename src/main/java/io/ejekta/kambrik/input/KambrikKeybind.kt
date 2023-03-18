package io.ejekta.kambrik.input

import io.ejekta.kambrik.ext.client.getBoundKey
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text

data class KambrikKeybind(
    val translation: String,
    val type: InputUtil.Type,
    val modKey: ModifiedKeyCode,
    val keyCategory: String, // TODO we already have a category var?
    val realTime: Boolean = false
) : KeyBinding(translation, type, modKey.keycode, keyCategory) {

    val boundKeyKey: InputUtil.Key
        get() = getBoundKey()

    data class ModifiedKeyCode(var keycode: Int, val modifiers: List<Int>) {
        fun areModifiersAllPressed(): Boolean {
            return modifiers.all {
                InputUtil.isKeyPressed(
                    MinecraftClient.getInstance().window.handle, it
                )
            }
        }
    }

    val moddedText: Text
        get() {
            val mKeys = (listOf(getBoundKey()) + modKey.modifiers.map {
                InputUtil.Type.KEYSYM.createFromCode(it)
            }).map {
                it.localizedText
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