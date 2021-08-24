package io.ejekta.kambrik.internal

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.Kambrik.Logger
import io.ejekta.kambrik.logging.KambrikMarkers
import io.ejekta.kambrik.ext.toMap
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrik.internal.testing.TellServerHello
import io.ejekta.kambrik.internal.testing.TestMsg
//import io.ejekta.kambrik.internal.testing.TestMsg
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import net.fabricmc.loader.api.metadata.CustomValue
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.*
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.filter.MarkerFilter

internal object KambrikMod : PreLaunchEntrypoint, ModInitializer {

    const val ID = "kambrik"

    fun idOf(unique: String) = Identifier(ID, unique)

    /*
    val config = KambrikConfigFile(
        FabricLoader.getInstance().configDir,
        "kambrik.json",
        Json {
            prettyPrint = true
            encodeDefaults = false
        },
        KambrikParseFailMode.OVERWRITE,
        KambrikInternalConfig.serializer(),
        ::KambrikInternalConfig
    )
     */

    override fun onPreLaunch() {
        Logger.info("Kambrik Says Hello!")
        handleCustomEntryData()
        configureLoggerFilters()
    }

    override fun onInitialize() {
        FabricLoader.getInstance().getEntrypointContainers(ID, KambrikMarker::class.java).forEach {
            Logger.debug("Got mod entrypoint: $it, ${it.entrypoint}, will do Kambrik init here")
            Logger.debug("It came from: ${it.provider.metadata.id}")
            KambrikRegistrar.doRegistrationFor(it)
        }
        CommandRegistrationCallback.EVENT.register(KambrikCommands)

        //TestMsg.Handler.register()






        Kambrik.Message.registerClientMessage(TestMsg.serializer(), Identifier("a", "b"))
        Kambrik.Message.registerServerMessage(TellServerHello.serializer(), Identifier("a", "c"))

        Kambrik.Command.addClientCommand("cambric") {
            executes {
                TellServerHello(BlockPos(5, 10, 15)).sendToServer()

                1
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

    fun configureLoggerFilters() {

        /*
        config.read().markers.let {
            KambrikMarkers.handleContainerMarkers(it)
        }

         */

        val ctx = LogManager.getContext(false) as LoggerContext
        ctx.reconfigure()

        for (logger in ctx.loggers.filterIsInstance<Logger>()) {
            println("Logger: ${logger.name}, ${logger.context.name}, ${ctx.name}, ${ctx.rootLogger.name}, ${ctx.loggers.map { it.name }}")
            logger.context.configuration.removeFilter(logger.context.configuration.filter)
            KambrikMarkers.Registry.forEach { (key, value) ->
                val filter = MarkerFilter.createFilter(key, if (value) Filter.Result.ACCEPT else Filter.Result.DENY, Filter.Result.NEUTRAL)
                logger.addFilter(filter)
            }
        }
    }

}