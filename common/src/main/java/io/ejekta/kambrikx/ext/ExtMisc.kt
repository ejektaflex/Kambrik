package io.ejekta.kambrikx.ext

import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState

// TODO evaluate necessity of this nowadays, might be obsolete and no reimpl needed
fun BlockState.rotated(times: Int): BlockState {
    var rot = Rotation.NONE

    val numRots = Rotation.entries.size
    val new = Rotation.entries[(rot.ordinal + times) % numRots]

    return rotate(new)
}