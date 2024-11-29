package io.ejekta.kambrik.ext

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level

/**
 * Scans the world, starting at one BlockPos and moving in one direction, until a condition is met
 * or the maximum distance is reached.
 */
fun Level.scanFor(
    startPos: BlockPos,
    direction: Direction = Direction.UP,
    maxDistance: Int = 64,
    until: Level.(pos: BlockPos) -> Boolean
): BlockPos? {
    var current: BlockPos

    for (i in 0..maxDistance) {
        current = startPos.relative(direction, i)
        if (until(current)) {
            return current
        }
    }

    return null
}