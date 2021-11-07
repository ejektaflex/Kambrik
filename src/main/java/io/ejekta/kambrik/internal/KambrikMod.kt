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

internal object KambrikMod : PreLaunchEntrypoint, ModInitializer {

    const val ID = "kambrik"
    
    val Logger = LogManager.getLogger("Kambrik")

    fun idOf(unique: String) = Identifier(ID, unique)

    override fun onPreLaunch() {
        Logger.info("Kambrik Says Hello!")
        handleCustomEntryData()
        configureLoggerFilters()
    }
    
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



    private fun handleCustomEntryData() {
        FabricLoader.getInstance().allMods.forEach { mod ->
            if (mod.metadata.containsCustomValue(ID)) {
                val cv = mod.metadata.getCustomValue(ID)
                if (cv is CustomValue.CvObject) {
                    for ((name, value) in cv) {
                        when (name) {
                            "log_markers" -> {
                                val markerMap = value.asObject.toMap().map { it.key to it.value.asBoolean }.toMap()
                                KambrikMarkers.handleContainerMarkers(markerMap)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun configureLoggerFilters() {
        val ctx = LogManager.getContext(false) as LoggerContext
        ctx.reconfigure()

        for (logger in ctx.loggers.filterIsInstance<Logger>()) {
            logger.context.configuration.removeFilter(logger.context.configuration.filter)
            KambrikMarkers.Registry.forEach { (key, value) ->
                val filter = MarkerFilter.createFilter(key, if (value) Filter.Result.ACCEPT else Filter.Result.DENY, Filter.Result.NEUTRAL)
                logger.addFilter(filter)
            }
        }
    }

}


