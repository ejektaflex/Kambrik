package io.ejekta.kambrikx.ext

import io.ejekta.kambrik.ext.math.axisValue
import io.ejekta.kambrik.ext.math.toVector3d
import io.ejekta.kambrik.ext.toVector3d
import io.ejekta.kambrikx.ext.vector.dirMask
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vector3d


fun Box.offsetBy(amt: Double, dir: Direction): Box {
    return offset(Vector3d(amt, amt, amt).dirMask(dir))
}

fun Box.getStart(): Vector3d {
    return Vector3d(minX, minY, minZ)
}

fun Box.getSize(): Vector3d {
    return Vector3d(maxX - minX, maxY - minY, maxZ - minZ)
}

fun Box.getEnd(): Vector3d {
    return Vector3d(maxX, maxY, maxZ)
}

// Returns the center point of the edge between the faces of the given directions
// NOTE: Opposite directions will return a zero vector
fun Box.edgeCenterPos(dirA: Direction, dirB: Direction): Vector3d {
    return center.add(
            (dirA.toVector3d().add(dirB.toVector3d()))
                    .multiply(getSize())
                    .multiply(0.5)
    )
}

fun Box.faceCenterPos(dir: Direction): Vector3d {
    return center.add(
            dir.toVector3d()
                    .multiply(getSize())
                    .multiply(0.5)
    )
}

fun Box.sizeOnAxis(axis: Direction.Axis): Double {
    return getSize().axisValue(axis)
}

fun Box.sizeInDirection(dir: Direction): Double {
    return getSize().axisValue(dir.axis)
}

fun Box.positionInDirection(dir: Direction): Double {
    return getStart().axisValue(dir.axis)
}

fun Box.longestAxisLength(): Double {
    val size = getSize()
    return enumValues<Direction.Axis>().maxByOrNull {
        size.getComponentAlongAxis(it)
    }!!.let { size.getComponentAlongAxis(it) }
}
