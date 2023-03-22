package io.ejekta.kambrik.bridge

var kambrik_loader_bridge = LoaderBridge<KambrikSharedApi>()

val Kambridge: KambrikSharedApi
    get() = kambrik_loader_bridge()