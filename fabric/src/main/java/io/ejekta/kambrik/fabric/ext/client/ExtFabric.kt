package io.ejekta.kambrik.fabric.ext.client

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping

fun KeyMapping.getBoundKey(): InputConstants.Key {
    return KeyBindingHelper.getBoundKeyOf(this)
}