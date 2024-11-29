package io.ejekta.kambrik.logging

import io.ejekta.kambrik.Kambrik
import net.minecraft.resources.ResourceLocation

data class KambrikMarker(val id: ResourceLocation) {
    companion object {
        val Rendering = KambrikMarker(Kambrik.idOf("rendering"))
        val NBT = KambrikMarker(Kambrik.idOf("nbt"))
    }
}