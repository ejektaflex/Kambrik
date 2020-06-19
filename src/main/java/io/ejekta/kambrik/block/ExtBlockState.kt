package io.ejekta.kambrik.block

import net.minecraft.block.BlockState
import net.minecraft.util.BlockRotation

fun BlockState.rotated(times: Int): BlockState {
    var rot = BlockRotation.NONE

    for (i in 0 until times % 4) {
        rot = rot.rotate(BlockRotation.CLOCKWISE_90)
    }

    return rotate(rot)
}