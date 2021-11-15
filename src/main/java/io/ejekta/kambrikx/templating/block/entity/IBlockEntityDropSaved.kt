package io.ejekta.kambrikx.templating.block.entity

import io.ejekta.kambrik.internal.KambrikExperimental
import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Used for blocks which save their NBT data into their respective itemstack
 */
@KambrikExperimental
abstract class KambrikBlockWithEntitySavesDrop(settings: Settings) : BlockWithEntity(settings) {

    abstract fun getBlockEntityType(): BlockEntityType<*>?

    abstract val entitySubTagName: String

    override fun getDroppedStacks(state: BlockState?, builder: LootContext.Builder?): MutableList<ItemStack> {
        val blockEntity = builder?.getNullable(LootContextParameters.BLOCK_ENTITY) ?: return mutableListOf()
        if (blockEntity.type == getBlockEntityType()) {
            return super.getDroppedStacks(state, builder).map {
                it.apply {
                    blockEntity.setStackNbt(it)
                }
            }.toMutableList()
        }
        return mutableListOf()
    }

    override fun onPlaced(
        world: World?,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        if (world != null && pos != null && itemStack != null && !world.isClient) {
            val blockEntity = world.getBlockEntity(pos, getBlockEntityType())
            blockEntity.ifPresent {
                val itemNbt = itemStack.nbt ?: return@ifPresent
                it.readNbt(itemNbt)
                it.markDirty()
            }
        }
    }

}