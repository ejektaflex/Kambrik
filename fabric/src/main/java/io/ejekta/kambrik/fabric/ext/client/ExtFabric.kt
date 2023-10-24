package io.ejekta.kambrik.fabric.ext.client

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

fun KeyBinding.getBoundKey(): InputUtil.Key {
    return KeyBindingHelper.getBoundKeyOf(this)
}