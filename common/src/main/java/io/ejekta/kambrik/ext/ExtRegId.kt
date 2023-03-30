package io.ejekta.kambrik.ext

import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

val Item.identifier: Identifier
    get() = Registries.ITEM.getId(this)

val ItemStack.identifier: Identifier
    get() = item.identifier

val Enchantment.identifier: Identifier?
    get() = Registries.ENCHANTMENT.getId(this)

val StatusEffect.identifier: Identifier?
    get() = Registries.STATUS_EFFECT.getId(this)

val Potion.identifier: Identifier
    get() = Registries.POTION.getId(this)

val EntityAttribute.identifier: Identifier?
    get() = Registries.ATTRIBUTE.getId(this)

val Block.identifier: Identifier
    get() = Registries.BLOCK.getId(this)

val Fluid.identifier: Identifier
    get() = Registries.FLUID.getId(this)

val EntityType<*>.identifier: Identifier
    get() = Registries.ENTITY_TYPE.getId(this)