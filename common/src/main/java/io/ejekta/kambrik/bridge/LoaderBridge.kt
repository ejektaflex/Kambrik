package io.ejekta.kambrik.bridge

open class LoaderBridge<API : Any>(fabricApi: String, forgeApi: String) {
    private var api: API

    operator fun invoke(): API {
        return api
    }

    init {
        val fabLoader = try {
            Class.forName("net.fabricmc.loader.api.FabricLoader")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
        api = if (fabLoader) {
            try {
                Class.forName(fabricApi).declaredConstructors.first().newInstance() as API
            } catch (e: Exception) {
                throw Exception("Kambrik could not load the Loader Bridge for $fabricApi!")
            }
        } else {
            try {
                Class.forName(forgeApi).declaredConstructors.first().newInstance() as API
            } catch (e: Exception) {
                throw Exception("Kambrik could not load the Forge Loader Bridge for $forgeApi!")
            }
        }
    }
}