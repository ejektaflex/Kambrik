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

    init {
        val key = RegistryKey.ofRegistry<Adornment>(
            Identifier(AdornmentMod.ID, "adornment")
        ) as RegistryKey<Registry<*>>
        (Registry.REGISTRIES as MutableRegistry<Registry<*>>).add(key, REGISTRY, REGISTRY.lifecycle)


        //(Registry.REGISTRIES as MutableRegistry<Registry<*>>).ad
    }

    val IRON = Adornment(Items.IRON_INGOT, 0xe0e0e0)
    val GOLD = Adornment(Items.GOLD_INGOT, 0xffc014)
    val DIAMOND = Adornment(Items.DIAMOND, 0x4deaff)
    val NETHERITE_SCRAP = Adornment(Items.NETHERITE_SCRAP, 0x4d443e)
    val EMERALD = Adornment(Items.EMERALD, 0x000c928)
    val LAPIS_LAZULI = Adornment(Items.LAPIS_LAZULI, 0x0079c9)
    //val GARNET = Adornment(GubbinsItems.GARNET, 0xff2b2b)
    //val AMETHYST = Adornment(GubbinsItems.AMETHYST, 0x9a1fff)
    //val ONYX = Adornment(GubbinsItems.ONYX, 0x262626)
}