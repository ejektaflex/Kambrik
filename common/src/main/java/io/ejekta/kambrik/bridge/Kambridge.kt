package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.Kambrik
import java.util.ServiceLoader

val Kambridge: KambrikSharedApi by lazy {
    Kambrik.Logger.info("Creating Kambrik Shared API...")
    val sls = ServiceLoader.load(KambrikSharedApi::class.java)

    Kambrik.Logger.info("Eh?")

    sls.findFirst().get().also { Kambrik.Logger.debug("Created API For: ${it.side}") }
}