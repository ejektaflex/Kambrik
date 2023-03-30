package io.ejekta.kambrik.ext.math

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d

fun MatrixStack.translate(vec3d: Vec3d) {
    translate(vec3d.x, vec3d.y, vec3d.z)
}


fun MatrixStack.scale(amt: Float) {
    scale(amt, amt, amt)
}


operator fun MatrixStack.plusAssign(vec3d: Vec3d) {
    translate(vec3d)
}
