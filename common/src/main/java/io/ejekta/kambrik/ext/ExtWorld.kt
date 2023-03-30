package io.ejekta.kambrik.ext

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

/**
 * Scans the world, starting at one BlockPos and moving in one direction, until a condition is met
 * or the maximum distance is reached.
 */
fun World.scanFor(
    startPos: BlockPos,
    direction: Direction = Direction.UP,
    maxDistance: Int = 64,
    until: World.(pos: BlockPos) -> Boolean
): BlockPos? {
    var current: BlockPos

    for (i in 0..maxDistance) {
        current = startPos.offset(direction, i)
        if (until(current)) {
            return current
        }
    }

    return null
}