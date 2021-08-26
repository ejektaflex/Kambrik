package io.ejekta.kambrik.internal.client

import io.ejekta.kambrik.Kambrik.Logger
import io.ejekta.kambrik.ext.fapi.toMap
import io.ejekta.kambrik.internal.KambrikMod.idOf
import io.ejekta.kambrikx.data.server.ServerDataRegistrar
import io.ejekta.kambrikx.data.serverData
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrik.logging.KambrikMarkers
import io.ejekta.kambrikx.data.config.ConfigDataRegistrar
import io.ejekta.kambrikx.data.configData
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import net.fabricmc.loader.api.metadata.CustomValue
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.filter.MarkerFilter

internal object KambrikClientMod : ClientModInitializer {

    override fun onInitializeClient() {

        // Client data lifecycle management

        ClientLifecycleEvents.CLIENT_STOPPING.register(ClientLifecycleEvents.ClientStopping {
            ConfigDataRegistrar.saveAllResults()
        })
    }

}


