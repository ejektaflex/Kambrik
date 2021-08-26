package io.ejekta.kambrikx.data.config

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.KambrikMod
import io.ejekta.kambrikx.data.LoadableDataRegistrar
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import java.io.File

internal object ConfigDataRegistrar : LoadableDataRegistrar() {

    init {
        loadResults()
    }

    override fun getFile(id: Identifier?): File {
        val namespace = id?.namespace ?: KambrikMod.ID

        val folder = Kambrik.File.getBaseFolder(namespace)

        println("FOLDER: $folder")

        return File(folder, "${namespace}_config.json")
    }

    override fun getRelatedObjects(id: Identifier): Map<Identifier, Any> {
        return loadedObjects.filter {
            id == null || id.namespace == it.key.namespace
        }
    }

}