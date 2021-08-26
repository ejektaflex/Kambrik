package io.ejekta.kambrikx.ext.vector

import io.ejekta.kambrikx.ext.toVec3d
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.round
import kotlin.math.abs

// Operator Functions

operator fun Vec3d.plus(other: Vec3d): Vec3d {
    return this.add(other)
}

operator fun Vec3d.minus(other: Vec3d): Vec3d {
    return this.subtract(other)
}

operator fun Vec3d.times(other: Vec3d): Vec3d {
    return this.multiply(other)
}

operator fun Vec3d.times(num: Double): Vec3d {
    return multiply(num)
}

// Conversion Functions

fun Vec3d.toArray(): DoubleArray {
    return doubleArrayOf(x, y, z)
}

fun Vec3d.toVec3i(): Vec3i {
    return Vec3i(x, y, z)
}


// Other Functions

fun Vec3d.abs(): Vec3d {
    return Vec3d(abs(x), abs(y), abs(z))
}

// Same as dirMask, but uses an absolute positive unit vector
fun Vec3d.axialMask(dir: Direction): Vec3d {
    return multiply(dir.toVec3d().abs())
}

fun Vec3d.axisValue(axis: Direction.Axis): Double {
    return axis.choose(x, y, z)
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

fun Vec3d.ceil(): Vec3i {
    return BlockPos(kotlin.math.ceil(x), kotlin.math.ceil(y), kotlin.math.ceil(z))
}

fun Vec3d.floor(): Vec3i {
    return BlockPos(this)
}

fun Vec3d.hasZeroAxis(): Boolean {
    return enumValues<Direction.Axis>().any {
        axisValue(it) == 0.0
    }
}

fun Vec3d.map(func: (it: Double) -> Double): Vec3d {
    return Vec3d(func(x), func(y), func(z))
}

fun Vec3d.projectedIn(dir: Direction, amt: Double): Vec3d {
    return add(Vec3d(amt, amt, amt).dirMask(dir))
}

fun Vec3d.rounded(): Vec3d {
    return Vec3d(round(x), round(y), round(z))
}
