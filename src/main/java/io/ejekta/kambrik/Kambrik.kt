package io.ejekta.kambrik

import io.ejekta.kambrik.command.KambrikCommandApi
import io.ejekta.kambrik.file.KambrikFileApi
import io.ejekta.kambrik.logging.KambrikLoggingApi
import io.ejekta.kambrik.message.KambrikMessageApi
import io.ejekta.kambrik.serial.KambrikSerialApi
import io.ejekta.kambrik.structure.KambrikStructureApi
import org.apache.logging.log4j.LogManager


object Kambrik {

    val Command = KambrikCommandApi()

    val Structure = KambrikStructureApi()

    val File = KambrikFileApi()

    val Logging = KambrikLoggingApi()

    val Message = KambrikMessageApi()

    val Serial = KambrikSerialApi()

    internal val Logger = LogManager.getLogger("Kambrik")

}
