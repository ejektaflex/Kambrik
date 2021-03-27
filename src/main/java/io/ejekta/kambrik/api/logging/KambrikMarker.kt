package io.ejekta.kambrik.api.logging

import io.ejekta.kambrik.KambrikMod
import net.minecraft.util.Identifier

data class KambrikMarker(val id: Identifier) {
    companion object {
        val Rendering = KambrikMarker(KambrikMod.idOf("rendering"))
        val NBT = KambrikMarker(KambrikMod.idOf("nbt"))
    }
}