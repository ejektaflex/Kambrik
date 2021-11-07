package io.ejekta.kambrikx.data

import kotlinx.serialization.KSerializer
import java.io.File

class ConfigDataFile(src: File) : DataFile(src) {
    private var loaded = false

    init {
        KambrikPersistence.configDataFiles.add(this)
    }

    // Do initial config file loading when the request is first made
    override fun <T : Any> request(key: String, serializer: KSerializer<T>, default: T) {
        super.request(key, serializer, default)
        if (!loaded) {
            load()
            loaded = true
        }
    }
}