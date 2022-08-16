package io.ejekta.kambrikx.ext.vector


import io.ejekta.kambrik.ext.math.abs
import io.ejekta.kambrik.ext.math.axisValue
import io.ejekta.kambrik.ext.toVector3d
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vector3d
import net.minecraft.util.math.Vec3i
import kotlin.math.abs


// Same as dirMask, but uses an absolute positive unit vector
fun Vector3d.axialMask(dir: Direction): Vector3d {
    return multiply(dir.toVector3d().abs())
}

// Masks a vector with a direction's unit vector
fun Vector3d.dirMask(dir: Direction): Vector3d {
    return multiply(dir.toVector3d())
}

/* 1 -> 0, 0 -> 1, used for [Vector3d::flipMask] */
private fun intSwitch(i: Int): Int {
    return (abs(i) - 1) * -1
}

fun Vector3d.flipMask(dir: Direction): Vector3d {
    val unit = dir.vector
    val mask = Vec3i(intSwitch(unit.x), intSwitch(unit.y), intSwitch(unit.z))
    return Vector3d(x * mask.x, y * mask.y, z * mask.z)
}

fun Vector3d.hasZeroAxis(): Boolean {
    return enumValues<Direction.Axis>().any {
        axisValue(it) == 0.0
    }
}

fun Vector3d.projectedIn(dir: Direction, amt: Double): Vector3d {
    return add(Vector3d(amt, amt, amt).dirMask(dir))
}
