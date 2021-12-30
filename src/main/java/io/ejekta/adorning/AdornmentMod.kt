package io.ejekta.adorning

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.kambrikx.ext.SpecialRecipes
import net.fabricmc.api.ModInitializer
import net.minecraft.item.ArmorItem
import net.minecraft.util.Identifier

object AdornmentMod : ModInitializer, KambrikAutoRegistrar {

    override fun onInitialize() {

        Kambrik.SpecialRecipes.addAnvilRecipe(
            Identifier(ID, "adorn_smithing")
        ) { armorStack, materialStack ->

            val material = Adornments.REGISTRY.getForMaterial(materialStack.item) ?: return@addAnvilRecipe null

            if (!armorStack.isEmpty && armorStack.item is ArmorItem) {
                val armorItem = armorStack.item as ArmorItem
                // Avoid repair ingredients and existing armor adornments
                if (armorItem.material.repairIngredient.test(materialStack)) return@addAnvilRecipe null
                if (armorStack.hasNbt() && armorStack.getNbt()!!.contains("_adornment")) return@addAnvilRecipe  null

                val output = armorStack.copy()
                output.orCreateNbt.putString("_adornment", Adornments.REGISTRY.getId(material).toString())
            }

            null
        }

        // TODO add the ability to remove adornments

    }

    val DISPLAY_ITEM_HELMET = "helmet" forItem AdornmentDisplay()
    val DISPLAY_ITEM_CHEST = "chest" forItem AdornmentDisplay()
    val DISPLAY_ITEM_LEGGINGS = "leggings" forItem AdornmentDisplay()
    val DISPLAY_ITEM_FEET = "feet" forItem AdornmentDisplay()

    const val ID = "adornments"
}