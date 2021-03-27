package io.ejekta.kambrik

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.CustomValue
import net.minecraft.util.Identifier
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator

object KambrikMod : ModInitializer {

    const val ID = "kambrik"

    fun idOf(unique: String) = Identifier(ID, unique)

    override fun onInitialize() {
        Kambrik.Logger.info("Kambrik Says Hello!")

        FabricLoader.getInstance().getEntrypointContainers(ID, KambrikMarker::class.java).forEach {
            Kambrik.Logger.debug("Got mod entrypoint: $it, ${it.entrypoint}, will do Kambrik init here")
            Kambrik.Logger.debug("It came from: ${it.provider.metadata.id}")
            KambrikRegistrar.doRegistrationFor(it)
        }

        println("Gonna Doot")
        doot()
        println("Dooted!")

        Kambrik.Logger.info("Kambrik has officially Dooted!")

        FabricLoader.getInstance().allMods.forEach { mod ->
            //println("Custom: ${mod.metadata.customValues.toString()}")
            if (mod.metadata.containsCustomValue(ID)) {
                val cv = mod.metadata.getCustomValue(ID)
                if (cv is CustomValue.CvObject) {

                }
            }
        }

    }

    fun doot() {
        val ctx = LogManager.getContext(false) as LoggerContext

        val builder = ConfigurationFactory.newConfigurationBuilder()
        builder.setStatusLevel(Level.DEBUG)
        builder.setConfigurationName("DootyLogger")

        val appBuilder = builder.newAppender("DootyApp", "CONSOLE").addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)

        appBuilder.add(
            builder.newLayout("PatternLayout").addAttribute("pattern", "%d %p %c [%t] %m%n")
        )

        //val rootLogger = builder.newRootLogger(Level.DEBUG)
        //rootLogger.add(builder.newAppenderRef("DootyApp"))

        builder.add(appBuilder)
        //builder.add(rootLogger)

        val conf = builder.build(true)

        println("Doot updated loggers")

        ctx.loggers.forEach { logger ->
            println("Had old logger: ${logger.name}")
        }

        ctx.updateLoggers(conf)
        ctx.reconfigure()

        for (logger in ctx.loggers) {
            println("Has new logger: ${logger.name}")
            //logger.addAppender(conf.getAppender("DootyApp"))
            logger.appenders.forEach { k, v ->
                println("  - Has appender: $k (${v.layout.contentType})")
            }
        }



        //val cfg = ctx.configuration.addAppender(null)


    }

}