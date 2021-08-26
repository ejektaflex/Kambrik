package io.ejekta.kambrik.file

import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Accessed via [Kambrik.File][io.ejekta.kambrik.Kambrik.File]
 */
class KambrikFileApi internal constructor() {

    fun getBaseFolderPath(modId: String): Path {
        return FabricLoader.getInstance().configDir.resolve(modId)
    }

    fun getBaseFolder(modId: String): File {
        return getBaseFolderPath(modId).toFile().apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

}