package io.ejekta.kambrik

import io.ejekta.kambrik.command.KambrikCommandApi
import io.ejekta.kambrik.criterion.KambrikCriterionApi
import io.ejekta.kambrik.file.KambrikFileApi
import io.ejekta.kambrik.logging.KambrikLoggingApi
import io.ejekta.kambrik.message.KambrikMessageApi
import io.ejekta.kambrik.serial.KambrikSerialApi
import io.ejekta.kambrik.structure.KambrikStructureApi
import io.ejekta.kambrik.input.KambrikInputApi
import org.apache.logging.log4j.LogManager


object Kambrik {

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

    val Criterion: KambrikCriterionApi by lazy {
        KambrikCriterionApi()
    }

}
