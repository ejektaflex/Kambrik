package io.ejekta.kambrik

import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack

object ModInit {

    data class Ratio(val top: Int, val bottom: Int)

    fun run() {

        /*
        For every itemstack, there is a list of ways that it can resolve to a map of stacks and their amounts
         */
        val stackCosts = mutableMapOf<ItemStack, List<Map<ItemStack, Ratio>>>()

        val game = MinecraftClient.getInstance()
        val server = game.server!!

        val recipes = server.recipeManager.values()

        for (recipe in recipes) {

            val inputs = recipe.ingredients


        }

    }

}