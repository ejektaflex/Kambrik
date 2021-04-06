package io.ejekta.kambrik.ext

import net.minecraft.item.ItemStack
import kotlin.math.min

fun List<ItemStack>.collect(amount: Int, func: ItemStack.() -> Boolean): Map<ItemStack, Int>? {
    val selected = mutableMapOf<ItemStack, Int>()
    var needed = amount
    for (stack in filter(func)) {
        val num = selected.getOrPut(stack) { 0 }

        val weNeed = min(needed, stack.count)
        selected[stack] = num + weNeed
        needed -= weNeed

        if (needed == 0) {
            return selected
        }
    }
    return null
}


