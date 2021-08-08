package io.ejekta.kambrik.templating.block.entity

import net.minecraft.block.BlockState
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Used for blocks which save their NBT data into their respective itemstack
 */
interface IBlockEntityDropSaved {

    fun getItemToSaveTo(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?): ItemStack

    fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?) {
        if (pos == null) return
        val be = world?.getBlockEntity(pos) ?: return
        val stack = getItemToSaveTo(world, pos, state, player).apply {
            if (nbt == null) {
                nbt = NbtCompound()
            }

            nbt!!.put("BlockEntityTag", be.writeNbt(NbtCompound()))
        }
        val entity = ItemEntity(
            world,
            player?.pos?.x ?: pos.x.toDouble(),
            player?.pos?.y ?: pos.y.toDouble(),
            player?.pos?.z ?: pos.z.toDouble(),
            stack
        ).apply {
            setToDefaultPickupDelay()
        }
        world.spawnEntity(entity)
    }

}