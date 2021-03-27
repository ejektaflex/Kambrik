package io.ejekta.kambrik.api.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager

class KambrikLogger private constructor(modid: String) {

    val level = Level.DEBUG

    val baseLogger = LogManager.getLogger(modid)



    fun info(msg: Any, markers: List<KambrikMarker>) {

    }

}