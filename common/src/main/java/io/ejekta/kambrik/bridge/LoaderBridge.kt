package io.ejekta.kambrik.bridge

open class LoaderBridge<API : Any> {
    private lateinit var api: API

    operator fun invoke(): API {
        return api
    }

    fun setupApi(newApi: API) {
        api = newApi
    }
}