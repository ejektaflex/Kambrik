package io.ejekta.kambrik.logging

import io.ejekta.kambrik.Kambrik
import net.minecraft.util.Identifier

data class KambrikMarker(val id: Identifier) {
    companion object {
        val Rendering = KambrikMarker(Kambrik.idOf("rendering"))
        val NBT = KambrikMarker(Kambrik.idOf("nbt"))
    }
}