package io.ejekta.kambrikx.ext.vector


import io.ejekta.kambrik.ext.math.abs
import io.ejekta.kambrik.ext.math.axisValue
import io.ejekta.kambrik.ext.toVec3d
import io.ejekta.kambrik.internal.KambrikExperimental
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.abs


// Same as dirMask, but uses an absolute positive unit vector
fun Vec3d.axialMask(dir: Direction): Vec3d {
    return multiply(dir.toVec3d().abs())
}

// Masks a vector with a direction's unit vector
fun Vec3d.dirMask(dir: Direction): Vec3d {
    return multiply(dir.toVec3d())
}

/* 1 -> 0, 0 -> 1, used for [Vec3d::flipMask] */
private fun intSwitch(i: Int): Int {
    return (abs(i) - 1) * -1
}

fun Vec3d.flipMask(dir: Direction): Vec3d {
    val unit = dir.vector
    val mask = Vec3i(intSwitch(unit.x), intSwitch(unit.y), intSwitch(unit.z))
    return Vec3d(x * mask.x, y * mask.y, z * mask.z)
}

fun Vec3d.hasZeroAxis(): Boolean {
    return enumValues<Direction.Axis>().any {
        axisValue(it) == 0.0
    }
}

fun Vec3d.projectedIn(dir: Direction, amt: Double): Vec3d {
    return add(Vec3d(amt, amt, amt).dirMask(dir))
}
