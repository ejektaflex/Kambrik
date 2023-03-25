package io.ejekta.kambrik.client

import net.minecraft.client.option.KeyBinding
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT


object KambrikModForgeClient {

    @SubscribeEvent @JvmStatic
    fun initClient(evt: FMLClientSetupEvent) {
        println("Kambrik setting up forge client init!")
    }

    val keysToRegister = mutableListOf<KeyBinding>()

    @SubscribeEvent
    fun registerKeys(evt: RegisterKeyMappingsEvent) {
        for (key in keysToRegister) {
            evt.register(key)
        }
    }

}