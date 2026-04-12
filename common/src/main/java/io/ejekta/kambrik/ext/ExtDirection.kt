package io.ejekta.kambrik.ext

import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

fun Direction.Axis.othersAxes(): List<Direction.Axis> {
    return enumValues<Direction.Axis>().filter { it != this }
}

fun Direction.rotatedClockwise(times: Int): Direction {
    return Direction.from2DDataValue((this.get2DDataValue() + times))
}

fun Direction.toVec3(): Vec3 {
    return Vec3(stepX.toDouble(), stepY.toDouble(), stepZ.toDouble())
}

