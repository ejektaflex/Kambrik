package io.ejekta.adorning

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.ext.SpecialRecipes
import net.minecraft.item.ItemStack

object AdornMixinHelper {
    fun smithingCanTake(stackA: ItemStack, stackB: ItemStack): ItemStack? {
        for (recipe in Kambrik.SpecialRecipes.anvilRecipes.values) {
            val result = recipe(stackA, stackB)
            result?.let {
                return it
            }
        }
        return null
    }
}