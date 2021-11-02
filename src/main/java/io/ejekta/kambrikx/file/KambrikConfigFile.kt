package io.ejekta.kambrikx.file

import io.ejekta.kambrik.ext.internal.assured
import io.ejekta.kambrik.internal.KambrikMod
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path

data class KambrikConfigFile<T>(val location: Path, val name: String, val format: Json = Json, val mode: KambrikParseFailMode, val serializer: KSerializer<T>, val default: () -> T) {

    fun getOrCreateFile(): File {
        return location.assured.resolve(name).toFile().apply {
            if (!exists()) {
                createNewFile()
                writeText(
                    format.encodeToString(serializer, default())
                )
            }
        }
    }

    fun ensureExistence() {
        getOrCreateFile()
    }

    fun read(): T {
        return with(getOrCreateFile()) {
            val contents = readText()
            try {
                format.decodeFromString(serializer, contents)
            } catch (e: Exception) {
                KambrikMod.Logger.warn("Kambrik could not correctly load config data at: $location => $name, reason: ${e.message}")
                KambrikMod.Logger.warn("Kambrik is set to ${mode.name} this file data for safety")

                e.printStackTrace()

                return when (mode) {
                    KambrikParseFailMode.LEAVE -> {
                        KambrikMod.Logger.warn("File will be left alone and default data will be loaded instead.")
                        default()
                    }
                    KambrikParseFailMode.OVERWRITE -> {
                        KambrikMod.Logger.warn("File will be overwritten with default data and loaded instead.")
                        write()
                        default()
                    }
                    KambrikParseFailMode.ERROR -> {
                        throw Exception("Game cannot proceed until the structure of the file has been fixed.")
                    }
                }
            }
        }
    }

    fun write(data: T? = null) {
        getOrCreateFile().run {
            val contents = format.encodeToString(serializer, data ?: default())
            writeText(contents)
        }
    }

    fun edit(func: T.() -> Unit): T {
        val data = read().apply(func)
        write(data)
        return data
    }

}

