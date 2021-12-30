package io.ejekta.adorning

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class Adornment(val recipeItem: Item, val colour: Int) {
    fun matches(stack: ItemStack): Boolean {
        return recipeItem === stack.item
    }
}