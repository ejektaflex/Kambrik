package io.ejekta.adorning

import io.ejekta.adorning.Adornment
import io.ejekta.adorning.AdornmentRegistry
import net.minecraft.util.registry.MutableRegistry
import io.ejekta.adorning.Adornments
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

object Adornments {
    val REGISTRY = AdornmentRegistry()

    val IRON = Adornment(Items.IRON_INGOT, 0xe0e0e0)
    val GOLD = Adornment(Items.GOLD_INGOT, 0xffc014)
    val DIAMOND = Adornment(Items.DIAMOND, 0x4deaff)
    val NETHERITE_SCRAP = Adornment(Items.NETHERITE_SCRAP, 0x4d443e)
    val EMERALD = Adornment(Items.EMERALD, 0x000c928)
    val LAPIS_LAZULI = Adornment(Items.LAPIS_LAZULI, 0x0079c9)

    init {
        listOf(IRON, GOLD, DIAMOND, NETHERITE_SCRAP, EMERALD, LAPIS_LAZULI).forEach {
            REGISTRY.add(
                RegistryKey.of(AdornmentRegistry.KEY, Identifier(AdornmentMod.ID, it.recipeItem.translationKey)),
                it
            )
        }

        (Registry.REGISTRIES as MutableRegistry<Registry<*>>).add(AdornmentRegistry.KEY as RegistryKey<Registry<*>>, REGISTRY, REGISTRY.lifecycle)
    }

    //val GARNET = Adornment(GubbinsItems.GARNET, 0xff2b2b)
    //val AMETHYST = Adornment(GubbinsItems.AMETHYST, 0x9a1fff)
    //val ONYX = Adornment(GubbinsItems.ONYX, 0x262626)
}