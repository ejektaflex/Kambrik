package io.ejekta.kambrik.ext.math

import io.ejekta.kambrik.ext.toVec3
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3

// Addition operator

operator fun Vec3i.plus(other: Vec3i): Vec3i {
    return offset(other)
}

operator fun BlockPos.plus(other: BlockPos): BlockPos {
    return BlockPos(this.asVec3i() + other)
}

operator fun Vec3.plus(other: Vec3): Vec3 {
    return add(other)
}

// Subtraction operator

operator fun Vec3i.minus(other: Vec3i): Vec3i {
    return subtract(other)
}

operator fun BlockPos.minus(other: BlockPos): BlockPos {
    return BlockPos(this.asVec3i() - other)
}

operator fun Vec3.minus(other: Vec3): Vec3 {
    return subtract(other)
}

// Unary minus operator

operator fun Vec3i.unaryMinus(): Vec3i {
    return Vec3i(-x, -y, -z)
}

operator fun Vec3.unaryMinus(): Vec3 {
    return Vec3(-x, -y, -z)
}

// Times operator

operator fun Vec3i.times(other: Vec3i): Vec3i {
    return Vec3i(x * other.x, y * other.y, z * other.z)
}

operator fun Vec3i.times(num: Int): Vec3i {
    return Vec3i(x * num, y * num, z * num)
}

operator fun Vec3.times(other: Vec3): Vec3 {
    return this.multiply(other)
}

operator fun Vec3.times(num: Double): Vec3 {
    return this.multiply(num, num, num)
}

// Masks a vector with a direction's unit vector

fun Vec3i.dirMask(dir: Direction): Vec3i {
    return (this * Vec3i(dir.stepX, dir.stepY, dir.stepZ))
}

fun BlockPos.dirMask(dir: Direction): BlockPos {
    return BlockPos(this * Vec3i(dir.stepX, dir.stepY, dir.stepZ))
}

fun Vec3.dirMask(dir: Direction): Vec3 {
    return multiply(dir.toVec3())
}

// Gets the value on an axis of a 3d coordinate

fun Vec3i.axisValue(axis: Direction.Axis): Int {
    return axis.choose(x, y, z)
}

fun BlockPos.axisValue(axis: Direction.Axis): Int {
    return this.asVec3i().axisValue(axis)
}

fun Vec3.axisValue(axis: Direction.Axis): Double {
    return axis.choose(x, y, z)
}
