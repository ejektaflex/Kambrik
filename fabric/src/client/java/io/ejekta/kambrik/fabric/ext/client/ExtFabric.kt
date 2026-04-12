package io.ejekta.kambrik.fabric.ext.client

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping

fun KeyMapping.getBoundKey(): InputConstants.Key {
    return KeyMappingHelper.getBoundKeyOf(this)
}
