package io.ejekta.kambrik.ext.math

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f

fun MatrixStack.translate(vec3d: Vec3d) {
    translate(vec3d.x, vec3d.y, vec3d.z)
}
fun MatrixStack.translate(vec3f: Vec3f) {
    translate(vec3f.x.toDouble(), vec3f.y.toDouble(), vec3f.z.toDouble())
}

fun MatrixStack.scale(vec3f: Vec3f) {
    scale(vec3f.x, vec3f.y, vec3f.z)
}

fun MatrixStack.scale(amt: Float) {
    scale(amt, amt, amt)
}
