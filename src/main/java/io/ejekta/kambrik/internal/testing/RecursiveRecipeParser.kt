package io.ejekta.kambrik.internal.testing

import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.server.MinecraftServer



class RecursiveRecipeParser(val server: MinecraftServer) {

    data class RecipeProcess(val makes: Int, val type: RecipeType<*>)



    val recipeManager: RecipeManager
        get() = server.recipeManager

    val visited = mutableMapOf<ItemStack, MutableList<Solveable>>()

    val ingredientCosts = mutableMapOf<Ingredient, MutableMap<ItemStack, Solveable>>()

    fun query(itemStack: ItemStack) {

        println("Querying $itemStack")

        val producers = recipeManager.values().filter {
            it.output.isItemEqual(itemStack)
        }

        println("Found ${producers.size} Producers")

        /*
        Gold Ingot
            Recipe: 9 Nuggets -> 1 Gold Ingot
                Gold Nugget is NOT in visited, so we visit it
            Recipe: 1 Gold Block -> 9 Gold Ingots

         */

        val visitList = visited.getOrPut(itemStack) {
            mutableListOf()
        }

        for (producer in producers) {
            println("\t* Processing Producer: ${producer.id}")

            val solveable = Solveable(producer.ingredients, producer.output.count, producer.type)

            visitList.add(solveable)

            // Visit all stacks
            for (input in producer.ingredients.map { it.matchingStacks.toList() }.flatten()) {
                if (input !in visited) {
                    query(input)
                }
            }
        }

        //vis
    }

}