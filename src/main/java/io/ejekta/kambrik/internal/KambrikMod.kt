package io.ejekta.kambrik.internal

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.fapi.toMap
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrik.logging.KambrikMarkers
import io.ejekta.kambrikx.data.ConfigDataFile
import io.ejekta.kambrikx.data.KambrikPersistence
import kotlinx.serialization.builtins.serializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
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

internal object KambrikMod : ModInitializer {

    const val ID = "kambrik"
    
    val Logger = LogManager.getLogger("Kambrik")

    fun idOf(unique: String) = Identifier(ID, unique)

    override fun onInitialize() {
        // Auto Registration feature
        FabricLoader.getInstance().getEntrypointContainers(ID, KambrikMarker::class.java).forEach {
            Logger.debug("Got mod entrypoint: $it, ${it.entrypoint} from ${it.provider.metadata.id}, will do Kambrik init here")
            KambrikRegistrar.doRegistrationFor(it)
        }

        // Kambrik commands
        CommandRegistrationCallback.EVENT.register(KambrikCommands)

        // Server data lifecycle management

        ServerLifecycleEvents.SERVER_STARTED.register {
            KambrikPersistence.loadServerResults()
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            KambrikPersistence.saveAllServerResults()

            if (FabricLoader.getInstance().environmentType != EnvType.CLIENT) {
                KambrikPersistence.saveAllConfigResults()
            }
        }
    }

}


