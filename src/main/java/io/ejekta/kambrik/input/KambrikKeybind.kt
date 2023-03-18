package io.ejekta.kambrik.input

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

@Serializable
data class KambrikKeybind(
    val translation: String,
    val type: InputUtil.Type,
    val key: Int,
    val category: String,
    val realTime: Boolean = false
) {

    @Transient var binding: KeyBinding = KeyBinding(translation, type, key, category)

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
            binding.isPressed = false
        }
    }

    init {
        if (realTime) {
            WorldRenderEvents.LAST.register(WorldRenderEvents.Last {
                update(binding.isPressed)
            })
        } else {
            ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
                update(binding.isPressed)
            })
        }
    }

}