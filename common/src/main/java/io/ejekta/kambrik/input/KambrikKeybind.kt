package io.ejekta.kambrik.input


import io.ejekta.kambrik.bridge.Kambridge
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

class KambrikKeybind(
    translation: String,
    type: InputUtil.Type,
    key: Int,
    category: String,
    val realTime: Boolean = false
) : KeyBinding(
    translation,
    type,
    key,
    category
) {


    private var keyDown = {}

    private var keyUp = {}

    fun onDown(func: () -> Unit) {
        keyDown = func
    }

    fun onUp(func: () -> Unit) {
        keyUp = func
    }

    var isDown = false
        private set

    private fun update(wasPressed: Boolean) {
        if (!isDown && wasPressed) {
            isDown = wasPressed
            keyDown()
        } else if (isDown && !wasPressed) {
            isDown = wasPressed
            keyUp()
            isPressed = false
        }
    }

    init {
//        if (realTime) {
//            Kambridge.hookKeybindUpdatesRealtime(this) {
//                update(isPressed)
//            }
//        } else {
//            Kambridge.hookKeybindUpdates(this) {
//                update(isPressed)
//            }
//        }
    }

}