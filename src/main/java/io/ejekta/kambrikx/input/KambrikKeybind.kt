package io.ejekta.kambrikx.input

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

class KambrikKeybind(
    translation: String,
    type: InputUtil.Type,
    key: Int,
    category: String,
    realTime: Boolean = false
) : KeyBinding(
    translation,
    type,
    key,
    category
) {

    val ourBoundKey: InputUtil.Key
        get() = KeyBindingHelper.getBoundKeyOf(this)

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

    fun update(wasPressed: Boolean) {
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