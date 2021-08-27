package io.ejekta.kambrik.ext.fapi

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.metadata.CustomValue
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

fun CustomValue.CvObject.toMap(): Map<String, CustomValue> {
    return associate { it.key to it.value }
}