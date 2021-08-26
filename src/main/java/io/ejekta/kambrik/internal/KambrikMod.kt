package io.ejekta.kambrik.internal

import io.ejekta.kambrik.Kambrik.Logger
import io.ejekta.kambrik.ext.fapi.toMap
import io.ejekta.kambrik.internal.data.ServerDataRegistrar
import io.ejekta.kambrik.internal.data.serverData
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrik.logging.KambrikMarkers
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

    fun idOf(unique: String) = Identifier(ID, unique)

    override fun onPreLaunch() {
        Logger.info("Kambrik Says Hello!")
        handleCustomEntryData()
        configureLoggerFilters()
    }
    
    var myIds: List<Identifier> by serverData(Identifier("kambrik", "ids"), listOf())

    override fun onInitialize() {
        FabricLoader.getInstance().getEntrypointContainers(ID, KambrikMarker::class.java).forEach {
            Logger.debug("Got mod entrypoint: $it, ${it.entrypoint}, will do Kambrik init here")
            Logger.debug("It came from: ${it.provider.metadata.id}")
            KambrikRegistrar.doRegistrationFor(it)
        }
        CommandRegistrationCallback.EVENT.register(KambrikCommands)

        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted {
            ServerDataRegistrar.loadResults(it)
        })

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

    fun configureLoggerFilters() {

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


