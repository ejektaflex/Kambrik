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

/**
 * Converts all properties of the Vector to an array
 */
fun Vec3d.toArray(): DoubleArray {
    return doubleArrayOf(x, y, z)
}


fun Vec3d.toBlockPos(): BlockPos {
    return BlockPos(toVec3i())
}


// Math Functions

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

fun min(vecA: Vec3d, vecB: Vec3d): Vec3d {
    return Vec3d(
        min(vecA.x, vecB.x),
        min(vecA.y, vecB.y),
        min(vecA.z, vecB.z)
    )
}

// Other Functions

fun Vec3d.axisValue(axis: Direction.Axis): Double {
    return axis.choose(x, y, z)
}

fun Vec3d.abs(): Vec3d {
    return Vec3d(abs(x), abs(y), abs(z))
}

fun Vec3d.ceil(): Vec3i {
    return BlockPos(ceil(x).toInt(), ceil(y).toInt(), ceil(z).toInt())
}

fun Vec3d.floor(): Vec3i {
    return BlockPos(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
}

fun Vec3d.map(func: (it: Double) -> Double): Vec3d {
    return Vec3d(func(x), func(y), func(z))
}

fun Vec3d.rounded(): Vec3d {
    return Vec3d(round(x), round(y), round(z))
}

