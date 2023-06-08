package io.ejekta.kambrik.bridge

import java.util.ServiceLoader

val Kambridge: KambrikSharedApi by lazy {
    ServiceLoader.load(KambrikSharedApi::class.java).findFirst().get()
}