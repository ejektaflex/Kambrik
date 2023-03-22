package io.ejekta.kambrik

import io.ejekta.kambrik.command.KambrikCommandApi
import io.ejekta.kambrik.file.KambrikFileApi
import io.ejekta.kambrik.logging.KambrikLoggingApi
import io.ejekta.kambrik.message.KambrikMessageApi
import io.ejekta.kambrik.serial.KambrikSerialApi
import io.ejekta.kambrik.structure.KambrikStructureApi
import io.ejekta.kambrik.input.KambrikInputApi
import io.ejekta.kambrik.internal.TestMsg
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager


object Kambrik {

    const val ID = "kambrik"


    fun idOf(unique: String) = Identifier(ID, unique)

    val Logger = LogManager.getLogger("Kambrik")

    val Command: KambrikCommandApi by lazy {
        KambrikCommandApi()
    }

    val Structure: KambrikStructureApi by lazy {
        KambrikStructureApi()
    }

    val File: KambrikFileApi by lazy {
        KambrikFileApi()
    }

    val Logging: KambrikLoggingApi by lazy {
        KambrikLoggingApi()
    }

    val Message: KambrikMessageApi by lazy {
        KambrikMessageApi()
    }

    val Serial: KambrikSerialApi by lazy {
        KambrikSerialApi()
    }

    val Input: KambrikInputApi by lazy {
        KambrikInputApi()
    }


}
