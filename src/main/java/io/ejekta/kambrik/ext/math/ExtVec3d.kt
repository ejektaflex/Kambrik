package io.ejekta.kambrik.ext.math

import net.minecraft.util.math.*
import kotlin.math.*

// Operator Functions

operator fun Vector3d.unaryMinus(): Vector3d {
    return Vector3d(-x, -y, -z)
}

operator fun Vector3d.plus(other: Vector3d): Vector3d {
    return this.add(other)
}

operator fun Vector3d.minus(other: Vector3d): Vector3d {
    return this.subtract(other)
}

operator fun Vector3d.times(other: Vector3d): Vector3d {
    return this.multiply(other)
}

operator fun Vector3d.times(num: Double): Vector3d {
    return multiply(num)
}

// Conversion Functions

/**
 * Converts all properties of the Vector to an array
 */
fun Vector3d.toArray(): DoubleArray {
    return doubleArrayOf(x, y, z)
}

fun Vector3d.toVec3i(): Vec3i {
    return Vec3i(x, y, z)
}

fun Vector3d.toBlockPos(): BlockPos {
    return BlockPos(this)
}

fun Vector3d.toVec3f(): Vec3f {
    return Vec3f(x.toFloat(), y.toFloat(), z.toFloat())
}

// Math Functions

fun List<Vector3d>.average(): Vector3d {
    return if (isEmpty()) {
        throw Exception("Cannot average an empty list of Vector3d! Must contain at least one element!")
    } else {
        val summed = this.reduce { a, b -> a.add(b) }
        summed.multiply(1.0 / size)
    }
}

fun max(vecA: Vector3d, vecB: Vector3d): Vector3d {
    return Vector3d(
        max(vecA.x, vecB.x),
        max(vecA.y, vecB.y),
        max(vecA.z, vecB.z)
    )
}

fun min(vecA: Vector3d, vecB: Vector3d): Vector3d {
    return Vector3d(
        min(vecA.x, vecB.x),
        min(vecA.y, vecB.y),
        min(vecA.z, vecB.z)
    )
}

// Other Functions

fun Vector3d.axisValue(axis: Direction.Axis): Double {
    return axis.choose(x, y, z)
}

fun Vector3d.abs(): Vector3d {
    return Vector3d(abs(x), abs(y), abs(z))
}

fun Vector3d.ceil(): Vec3i {
    return BlockPos(ceil(x), ceil(y), ceil(z))
}

fun Vector3d.floor(): Vec3i {
    return BlockPos(this)
}

fun Vector3d.map(func: (it: Double) -> Double): Vector3d {
    return Vector3d(func(x), func(y), func(z))
}

fun Vector3d.rounded(): Vector3d {
    return Vector3d(round(x), round(y), round(z))
}

