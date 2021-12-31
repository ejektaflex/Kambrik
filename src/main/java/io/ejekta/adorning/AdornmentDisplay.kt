package io.ejekta.adorning

import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class AdornmentDisplay : Item(Settings().maxCount(1)) {
    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        //stack.count = 0
    }
}