package io.ejekta.kambrikx.recipe

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

typealias KambrikCustomAnvilRecipe =  (stackA: ItemStack, stackB: ItemStack) -> ItemStack?

/**
 * This API allows you to add non-standard special recipes to the game.
 * Namely, this should be used for recipes that cannot be expressed with
 * vanilla json files (leather dying and armor repair are good examples).
 */
class KambrikSpecialRecipeApi internal constructor() {

    internal val anvilRecipes = mutableMapOf<Identifier, KambrikCustomAnvilRecipe>()

    val AnvilRecipes: Map<Identifier, KambrikCustomAnvilRecipe>
        get() = anvilRecipes.toMap()

    fun addAnvilRecipe(id: Identifier, func: KambrikCustomAnvilRecipe) {
        anvilRecipes[id] = func
    }

}