package io.ejekta.kambrik


import io.ejekta.kambrik.input.KambrikModifiedBind

class KambrikKeybind(
    val keyMod: KambrikModifiedBind,
    val realTime: Boolean = false
) {

    private var keyDown = {}

    private var keyUp = {}

    private var keyRepeat = {}

    private fun getPressState(): Boolean {
        return keyMod.getIsPressed()
    }

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

    fun update(wasPressed: Boolean = getPressState()) {
        if (!isDown && wasPressed) {
            isDown = wasPressed
            keyDown()
        } else if (isDown && !wasPressed) {
            isDown = wasPressed
            keyUp()
            // TODO re-enable??
            //isPressed = false
        }
    }

//    init {
//        if (realTime) {
//            WorldRenderEvents.LAST.register(WorldRenderEvents.Last {
//                update(getPressState())
//            })
//        } else {
//            ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
//                update(getPressState())
//            })
//        }
//    }

}