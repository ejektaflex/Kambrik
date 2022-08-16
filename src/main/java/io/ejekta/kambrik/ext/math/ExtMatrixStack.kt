package io.ejekta.kambrik.ext.math

import com.mojang.math.Vector3d
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f

fun PoseStack.translate(Vector3d: Vector3d) {
    translate(Vector3d.x, Vector3d.y, Vector3d.z)
}
fun PoseStack.translate(vec3f: Vector3f) {
    translate(vec3f.x().toDouble(), vec3f.y().toDouble(), vec3f.z().toDouble())
}

fun PoseStack.scale(vec3f: Vector3f) {
    scale(vec3f.x(), vec3f.y(), vec3f.z())
}

fun PoseStack.scale(amt: Float) {
    scale(amt, amt, amt)
}

operator fun PoseStack.timesAssign(quat: Quaternion) {
    mulPose(quat)
}

operator fun PoseStack.plusAssign(Vector3d: Vector3d) {
    translate(Vector3d)
}

operator fun PoseStack.plusAssign(vec3f: Vector3f) {
    translate(vec3f)
}