package io.ejekta.kambrik.ext

import net.minecraft.entity.LivingEntity

fun LivingEntity.healFully() {
    heal(maxHealth - health)
}