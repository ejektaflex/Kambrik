package io.ejekta.kambrik

import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.fluid.Fluid
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

val ItemStack.identifier: Identifier
    get() = Registry.ITEM.getId(item)

val Enchantment.identifier: Identifier?
    get() = Registry.ENCHANTMENT.getId(this)

val StatusEffect.identifier: Identifier?
    get() = Registry.STATUS_EFFECT.getId(this)

val Potion.identifier: Identifier
    get() = Registry.POTION.getId(this)

val EntityAttribute.identifier: Identifier?
    get() = Registry.ATTRIBUTE.getId(this)

val Block.identifier: Identifier
    get() = Registry.BLOCK.getId(this)

val Fluid.identifier: Identifier
    get() = Registry.FLUID.getId(this)

