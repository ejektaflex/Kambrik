package io.ejekta.kambrik

import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.command.KambrikCommandApi
import io.ejekta.kambrik.criterion.KambrikCriterionApi
import io.ejekta.kambrik.file.KambrikFileApi
import io.ejekta.kambrik.logging.KambrikLoggingApi
import io.ejekta.kambrik.message.KambrikMessageApi
import io.ejekta.kambrik.serial.KambrikSerialApi
import io.ejekta.kambrik.structure.KambrikStructureApi
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import java.util.*


object Kambrik {

    const val ID = "kambrik"

//    val Bridge: KambrikSharedApi
//        get() {
//            println("Getting bridge!")
//
//            val sl = ServiceLoader.load(KambrikSharedApi::class.java)
//
//
//            println("Got SL?")
//
//            return sl.single()
//        }

    fun idOf(unique: String) = Identifier(ID, unique)

    val Logger = LogManager.getLogger("Kambrik")

    val Criterion: KambrikCriterionApi by lazy {
        KambrikCriterionApi()
    }

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

}
