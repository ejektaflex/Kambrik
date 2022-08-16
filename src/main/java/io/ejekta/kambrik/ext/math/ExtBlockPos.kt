package io.ejekta.kambrik.ext.math

import com.mojang.math.Vector3d
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i

operator fun BlockPos.plus(other: Vec3i): BlockPos {
    return offset(other.x, other.y, other.z)
}

operator fun BlockPos.minus(other: Vec3i): BlockPos {
    return subtract(other)
}

operator fun BlockPos.times(int: Int): BlockPos {
    return BlockPos(x * int, y * int, z * int)
}

fun BlockPos.toVector3d(): Vector3d {
    return Vector3d(x.toDouble(), y.toDouble(), z.toDouble())
}