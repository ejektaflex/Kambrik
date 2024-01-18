package io.ejekta.kambrik.neoforge.client

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent


object KambrikModForgeClient {

    @SubscribeEvent
    @JvmStatic
    fun initClient(evt: FMLClientSetupEvent) {
        // nothing to do right now
    }


}