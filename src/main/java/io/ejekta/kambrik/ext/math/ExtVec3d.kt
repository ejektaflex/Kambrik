package io.ejekta.kambrik.ext.math

import net.minecraft.util.math.*
import kotlin.math.*

// Operator Functions

operator fun Vec3d.unaryMinus(): Vec3d {
    return Vec3d(-x, -y, -z)
}

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

fun Vec3d.toBlockPos(): BlockPos {
    return BlockPos(this)
}

fun Vec3d.toVec3f(): Vec3f {
    return Vec3f(x.toFloat(), y.toFloat(), z.toFloat())
}

// Other Functions

fun Vec3d.axisValue(axis: Direction.Axis): Double {
    return axis.choose(x, y, z)
}

fun Vec3d.abs(): Vec3d {
    return Vec3d(abs(x), abs(y), abs(z))
}

fun Vec3d.ceil(): Vec3i {
    return BlockPos(ceil(x), ceil(y), ceil(z))
}

fun Vec3d.floor(): Vec3i {
    return BlockPos(this)
}

fun Vec3d.map(func: (it: Double) -> Double): Vec3d {
    return Vec3d(func(x), func(y), func(z))
}

fun Vec3d.rounded(): Vec3d {
    return Vec3d(round(x), round(y), round(z))
}

