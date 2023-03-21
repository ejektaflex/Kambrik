package io.ejekta.kambrik.bridge

import net.minecraft.client.option.KeyBinding

interface LoaderApi {

    fun registerKeybind(kb: KeyBinding)

}