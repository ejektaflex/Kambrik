package io.ejekta.kambrik.ext.math

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vec3i
import kotlin.math.*

// Operator Functions

operator fun Vec3f.unaryMinus(): Vec3f {
    return Vec3f(-x, -y, -z)
}

operator fun Vec3f.plus(other: Vec3f): Vec3f {
    return copy().apply {
        add(other)
    }
}

operator fun Vec3f.minus(other: Vec3f): Vec3f {
    return copy().apply {
        subtract(other)
    }
}

operator fun Vec3f.times(other: Vec3f): Vec3f {
    return copy().apply {
        multiplyComponentwise(other.x, other.y, other.z)
    }
}

operator fun Vec3f.times(num: Float): Vec3f {
    return copy().apply {
        multiplyComponentwise(num, num, num)
    }
}

// Conversion Functions

/**
 * Converts all properties of the Vector to an array
 */
fun Vec3f.toArray(): FloatArray {
    return floatArrayOf(x, y, z)
}

/**
 * Converts the Vec3f to a Vec3d
 */
fun Vec3f.toVec3d(): Vec3d {
    return Vec3d(this)
}

/**
 * Converts the Vec3f to a Vec3i
 */
fun Vec3f.toVec3i(): Vec3i {
    return Vec3i(x.toInt(), y.toInt(), z.toInt())
}

/**
 * Converts the Vec3f to a BlockPos
 */
fun Vec3f.toBlockPos(): BlockPos {
    return BlockPos(x.toInt(), y.toInt(), z.toInt())
}

// Other Functions

fun Vec3f.abs(): Vec3f {
    return Vec3f(abs(x), abs(y), abs(z))
}

fun Vec3f.ceil(): Vec3i {
    return BlockPos(ceil(x).toInt(), ceil(y).toInt(), ceil(z).toInt())
}

fun Vec3f.floor(): Vec3i {
    return BlockPos(x.toInt(), y.toInt(), z.toInt())
}

fun Vec3f.map(func: (it: Float) -> Float): Vec3f {
    return Vec3f(func(x), func(y), func(z))
}

fun Vec3f.rounded(): Vec3f {
    return Vec3f(round(x), round(y), round(z))
}


