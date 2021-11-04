package io.ejekta.kambrik.ext.math

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

operator fun BlockPos.plus(other: Vec3i): BlockPos {
    return add(other.x, other.y, other.z)
}

operator fun BlockPos.minus(other: Vec3i): BlockPos {
    return subtract(other)
}

operator fun BlockPos.times(int: Int): BlockPos {
    return BlockPos(x * int, y * int, z * int)
}

fun BlockPos.toVec3d(): Vec3d {
    return Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
}