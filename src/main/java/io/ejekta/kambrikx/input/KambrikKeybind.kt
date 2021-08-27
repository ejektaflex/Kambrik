package io.ejekta.kambrikx.input

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

class KambrikKeybind(
    translation: String,
    type: InputUtil.Type,
    key: Int,
    category: String
) : KeyBinding(
    translation,
    type,
    key,
    category
) {

    private var keyDown = {}

    private var keyUp = {}

    private var keyHeld = {}

    fun onDown(func: () -> Unit) {
        keyDown = func
    }

    fun onUp(func: () -> Unit) {
        keyUp = func
    }

    fun onHolding(func: () -> Unit) {
        keyHeld = func
    }

    var isDown = false
        private set

    fun update(wasPressed: Boolean) {
        if (!isDown && wasPressed) {
            isDown = wasPressed
            keyDown()
        } else if (isDown && !wasPressed) {
            isDown = wasPressed
            keyUp()
        } else if (isDown) {
            keyHeld()
        }
    }


    init {
        ClientTickEvents.END_CLIENT_TICK.register {
            update(wasPressed())
        }
    }


}