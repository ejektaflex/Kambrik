package io.ejekta.kambrikx.data.config

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.KambrikMod
import io.ejekta.kambrikx.data.LoadableDataRegistrar
import kotlinx.serialization.KSerializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import java.io.File

internal object ConfigDataRegistrar : LoadableDataRegistrar() {

    val loadedModConfigs = mutableSetOf<String>()

    // Do initial config file loading when the request is first made
    override fun <T : Any> request(key: Identifier, serializer: KSerializer<T>, default: T) {
        super.request(key, serializer, default)
        if (key.namespace !in loadedModConfigs) {
            loadResults(key)
        }
    }

    fun saveAllResults() {
        loadedObjects.keys.map { it.namespace }.forEach {
            saveResults(Identifier(it, "unused"))
        }
    }

    override fun getFile(id: Identifier?): File {
        val namespace = id?.namespace ?: KambrikMod.ID

        val folder = Kambrik.File.getBaseFolder(namespace)

        return File(folder, "${namespace}_config.json")
    }

    override fun getRelatedObjects(id: Identifier): Map<Identifier, Any> {
        return loadedObjects.filter {
            id == null || id.namespace == it.key.namespace
        }
    }

}