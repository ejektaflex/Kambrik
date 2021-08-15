package io.ejekta.kambrik.ext

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

fun Direction.Axis.othersAxes(): List<Direction.Axis> {
    return enumValues<Direction.Axis>().filter { it != this }
}

fun Direction.rotatedClockwise(times: Int): Direction {
    return Direction.fromHorizontal((this.horizontal + times))
}

fun Direction.toVec3d(): Vec3d {
    return Vec3d(vector.x.toDouble(), vector.y.toDouble(), vector.z.toDouble())
}

