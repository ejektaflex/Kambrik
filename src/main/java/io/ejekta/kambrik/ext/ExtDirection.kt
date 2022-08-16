package io.ejekta.kambrik.ext

import com.mojang.math.Vector3d
import net.minecraft.core.Direction

fun Direction.Axis.othersAxes(): List<Direction.Axis> {
    return enumValues<Direction.Axis>().filter { it != this }
}

fun Direction.rotatedClockwise(times: Int): Direction {
    return Direction.from2DDataValue((this.get2DDataValue() + times))
}

fun Direction.toVector3d(): Vector3d {
    return Vector3d(normal.x.toDouble(), normal.y.toDouble(), normal.z.toDouble())
}

