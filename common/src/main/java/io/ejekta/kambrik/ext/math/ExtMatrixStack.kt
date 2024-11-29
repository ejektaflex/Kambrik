package io.ejekta.kambrik.ext.math

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.phys.Vec3

fun PoseStack.translate(vec3: Vec3) {
    translate(vec3.x, vec3.y, vec3.z)
}


fun PoseStack.scale(amt: Float) {
    scale(amt, amt, amt)
}


operator fun PoseStack.plusAssign(vec3d: Vec3) {
    translate(vec3d)
}
