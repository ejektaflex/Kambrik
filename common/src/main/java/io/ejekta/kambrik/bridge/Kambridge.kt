package io.ejekta.kambrik.bridge

val Kambridge by lazy {
    LoaderBridge<KambrikSharedApi>(
        "io.ejekta.kambrik.bridge.KambrikSharedApiFabric",
        "io.ejekta.kambrik.bridge.KambrikSharedApiForge"
    )()
}