package io.ejekta.kambrik.api.logging

import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.LoggerContext

class KambrikLoggingApi internal constructor() {

    init {
        val ctx = LogManager.getContext(false) as LoggerContext
        ctx.reconfigure()
    }

    fun createLogger(modid: String): Logger {
        return LogManager.getLogger(modid)
    }

    fun createMarker(id: Identifier): Marker {
        return MarkerManager.getMarker(id.toString())
    }

}