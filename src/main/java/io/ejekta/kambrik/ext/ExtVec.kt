package io.ejekta.kambrik.ext

import net.minecraft.util.math.Vec3d
import kotlin.math.max

fun List<Vec3d>.average(): Vec3d {
    return if (isEmpty()) {
        throw Exception("Cannot average an empty list of Vec3d! Must contain at least one element!")
    } else {
        val summed = this.reduce { a, b -> a.add(b) }
        summed.multiply(1.0 / size)
    }
}

fun max(vecA: Vec3d, vecB: Vec3d): Vec3d {
    return Vec3d(
            max(vecA.x, vecB.x),
            max(vecA.y, vecB.y),
            max(vecA.z, vecB.z)
    )
}



