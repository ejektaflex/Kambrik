package io.ejekta.kambrik.bridge

object LoaderBridge {
    private lateinit var api: LoaderApi

    operator fun invoke(): LoaderApi {
        return api
    }

    fun setupApi(newApi: LoaderApi) {
        api = newApi
    }
}