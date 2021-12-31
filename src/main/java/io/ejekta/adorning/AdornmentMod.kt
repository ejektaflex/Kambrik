package io.ejekta.adorning

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.kambrikx.ext.SpecialRecipes
import net.fabricmc.api.ModInitializer
import net.minecraft.item.ArmorItem
import net.minecraft.util.Identifier

object AdornmentMod : ModInitializer, KambrikAutoRegistrar {

    override fun onInitialize() {

        println("Adornment init!")

        Kambrik.SpecialRecipes.addAnvilRecipe(
            Identifier(ID, "adorn_smithing")
        ) { armorStack, materialStack ->

            println("Check?: $armorStack $materialStack")

            val material = Adornments.REGISTRY.getForMaterial(materialStack.item) ?: return@addAnvilRecipe null

            println("Check material: $material")

            if (!armorStack.isEmpty && armorStack.item is ArmorItem) {
                println("Check armor: is armor stack")
                val armorItem = armorStack.item as ArmorItem
                // Avoid repair ingredients and existing armor adornments
                if (armorItem.material.repairIngredient.test(materialStack)) return@addAnvilRecipe null
                println("Check repair: is not repair")
                if (armorStack.hasNbt() && armorStack.nbt!!.contains("_adornment")) return@addAnvilRecipe  null
                println("Check adorns: none")
                val output = armorStack.copy()
                output.orCreateNbt.putString("_adornment", Adornments.REGISTRY.getId(material).toString())
                return@addAnvilRecipe output
            }

            return@addAnvilRecipe null
        }

        // TODO add the ability to remove adornments

    }

    val DISPLAY_ITEM_HELMET = "helmet" forItem AdornmentDisplay()
    val DISPLAY_ITEM_CHEST = "chest" forItem AdornmentDisplay()
    val DISPLAY_ITEM_LEGGINGS = "leggings" forItem AdornmentDisplay()
    val DISPLAY_ITEM_FEET = "feet" forItem AdornmentDisplay()

    const val ID = "adornments"
}