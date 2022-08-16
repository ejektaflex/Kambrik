package io.ejekta.kambrik.ext

import net.minecraft.world.entity.LivingEntity

fun LivingEntity.healFully() {
    heal(maxHealth - health)
}