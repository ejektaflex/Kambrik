package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.input.KambrikKeybind
import net.minecraft.client.option.KeyBinding

interface LoaderApi {

    fun registerKeybind(kb: KeyBinding)

    fun hookKeybindUpdates(kambrikKeybind: KambrikKeybind, func: KambrikKeybind.() -> Unit)

    fun hookKeybindUpdatesRealtime(kambrikKeybind: KambrikKeybind, func: KambrikKeybind.() -> Unit)

}