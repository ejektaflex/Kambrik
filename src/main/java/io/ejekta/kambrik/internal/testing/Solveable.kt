package io.ejekta.kambrik.internal.testing

import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeType

data class Solveable(val ingredients: List<Ingredient>, val makes: Int, val type: RecipeType<*>) {



    fun solveDependencies(parser: RecursiveRecipeParser): List<Int> {
        val seen: MutableList<ItemStack> = mutableListOf()
        val needed = ingredients.map { it.matchingStacks.toList() }.flatten()

        for (need in needed) {
            val found = parser.visited[need]!!
        }

        return emptyList()
    }

    fun solve(parser: RecursiveRecipeParser, deps: MutableMap<ItemStack, Int> = mutableMapOf()): MutableMap<ItemStack, Int> {
        val needed = ingredients.map { it.matchingStacks.toList() }.flatten()
        val resolvedSolves = needed.asSequence().filter { it !in deps }.map {
            val old = deps.getOrPut(it) { 0 }
            deps[it] = old + 1
            parser.visited[it]!!
        }.flatten().toSet().toList()

        resolvedSolves.forEach {
            it.solve(parser, deps)
        }

        return deps
    }

}