package io.ejekta.kambrik.ext.math

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3

fun BlockPos.asVec3i(): Vec3i {
    return this as Vec3i
}

fun BlockPos.toVec3(): Vec3 {
    return Vec3(x.toDouble(), y.toDouble(), z.toDouble())
}
