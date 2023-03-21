package io.ejekta.kambrikx.data

import java.io.File

class ServerDataFile(src: File) : DataFile(src) {
    private var loaded = false

    init {
        KambrikPersistence.serverDataFiles.add(this)
    }

    override fun load() {
        super.load()
        loaded = true
    }

    override fun save() {
        super.save()
        loaded = false
    }

    override fun <R : Any> loadResult(key: String): R {
        if (!loaded) {
            throw Exception("Trying to access server data before the server is loaded!")
        }
        return super.loadResult(key)
    }
}