package io.ejekta.kambrikx.ext

import io.ejekta.kambrik.ext.math.axisValue
import io.ejekta.kambrik.ext.math.dirMask
import io.ejekta.kambrik.ext.math.times
import io.ejekta.kambrik.ext.toVec3
import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3


fun AABB.move(amt: Double, dir: Direction): AABB {
    return move(Vec3(amt, amt, amt).dirMask(dir))
}

fun AABB.getStart(): Vec3 {
    return Vec3(minX, minY, minZ)
}

fun AABB.getVolume(): Vec3 {
    return Vec3(maxX - minX, maxY - minY, maxZ - minZ)
}

fun AABB.getEnd(): Vec3 {
    return Vec3(maxX, maxY, maxZ)
}

// Returns the center point of the edge between the faces of the given directions
// NOTE: Opposite directions will return a zero vector
fun AABB.edgeCenterPos(dirA: Direction, dirB: Direction): Vec3 {
    return center.add(
            (dirA.toVec3().add(dirB.toVec3()))
                    .multiply(getVolume())
                    .times(0.5)
    )
}

fun AABB.faceCenterPos(dir: Direction): Vec3 {
    return center.add(
            dir.toVec3()
                    .multiply(getVolume())
                    .times(0.5)
    )
}

fun AABB.sizeOnAxis(axis: Direction.Axis): Double {
    return getVolume().axisValue(axis)
}

fun AABB.sizeInDirection(dir: Direction): Double {
    return getVolume().axisValue(dir.axis)
}

fun AABB.positionInDirection(dir: Direction): Double {
    return getStart().axisValue(dir.axis)
}

fun AABB.longestAxisLength(): Double {
    val volume = getVolume()
    return enumValues<Direction.Axis>().maxByOrNull {
        volume.axisValue(it)
    }!!.let { volume.axisValue(it) }
}
