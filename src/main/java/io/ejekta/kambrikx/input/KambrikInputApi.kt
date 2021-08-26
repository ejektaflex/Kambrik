package io.ejekta.kambrikx.input

import io.ejekta.kambrik.internal.KambrikExperimental
import io.ejekta.kambrikx.input.keybind.KambrikKeybindingApi

class KambrikInputApi internal constructor() {

    @OptIn(KambrikExperimental::class)
    val Keybinding = KambrikKeybindingApi()

}