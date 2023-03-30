package io.ejekta.kambrik.file

import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Accessed via [Kambrik.File][io.ejekta.kambrik.Kambrik.File]
 */
class KambrikFileApi internal constructor() {

    fun getConfigFolderRelativePath(modId: String): Path {
        return Paths.get("config", modId)
    }

    fun getConfigFolderAbsolutePath(modId: String): Path {
        return FabricLoader.getInstance().configDir.resolve(modId)
    }

    fun getBaseFile(modId: String): File {
        return FabricLoader.getInstance().configDir.resolve("${modId}.json").toFile().apply {
            if (!exists()) {
                createNewFile()
            }
        }
    }

    fun getBaseFolder(modId: String): File {
        return getConfigFolderRelativePath(modId).toFile().apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

}