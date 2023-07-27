package io.ejekta.kambrik.client

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent


object KambrikModForgeClient {

    @SubscribeEvent @JvmStatic
    fun initClient(evt: FMLClientSetupEvent) {
        // nothing to do right now
    }


}