package io.ejekta.adorning

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.ext.SpecialRecipes
import net.minecraft.item.ItemStack

object MixinHelper {
    fun smithingCanTake(stackA: ItemStack, stackB: ItemStack): ItemStack? {
        println("Take check.. $stackA $stackB")
        for (recipe in Kambrik.SpecialRecipes.anvilRecipes.values) {
            val result = recipe(stackA, stackB)
            println("Result: $result")
            if (result != null) {
                println("Returning it")
                return result
            }
        }
        return null
    }
}