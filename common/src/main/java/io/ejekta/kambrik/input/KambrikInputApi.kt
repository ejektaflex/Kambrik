package io.ejekta.kambrik.input

import io.ejekta.kambrik.KambrikKeybind

class KambrikInputApi internal constructor() {

    private val keyBinds = mutableListOf<KambrikKeybind>()

    fun updateNormBinds() {
        for (bind in keyBinds) {
            if (!bind.realTime) {
                bind.update()
            }
        }
    }

    fun updateRealBinds() {
        for (bind in keyBinds) {
            if (bind.realTime) {
                bind.update()
            }
        }
    }

    fun registerBinding(
        key: KambrikModifiedBind,
        realTime: Boolean = false,
        bindingDsl: KambrikKeybind.() -> Unit
    ): KambrikKeybind {
        val kambrikKeybind = KambrikKeybind(
            key, realTime
        ).apply(bindingDsl)
        keyBinds.add(kambrikKeybind)
        return kambrikKeybind
    }

}