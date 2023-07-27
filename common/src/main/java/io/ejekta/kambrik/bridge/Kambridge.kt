package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.Kambrik
import java.util.ServiceLoader

val Kambridge: KambrikSharedApi by lazy {
    Kambrik.Logger.debug("Creating Kambrik Shared API...")
    ServiceLoader.load(KambrikSharedApi::class.java).findFirst().get().also { Kambrik.Logger.debug("Created API For: ${it.side}") }
}