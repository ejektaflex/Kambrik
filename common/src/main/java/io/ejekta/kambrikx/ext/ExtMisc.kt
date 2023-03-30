package io.ejekta.kambrikx.ext

import net.minecraft.block.BlockState
import net.minecraft.util.BlockRotation

fun BlockState.rotated(times: Int): BlockState {
    var rot = BlockRotation.NONE

    val numRots = BlockRotation.values().size
    val new = BlockRotation.values()[(rot.ordinal + times) % numRots]

    return rotate(new)
}