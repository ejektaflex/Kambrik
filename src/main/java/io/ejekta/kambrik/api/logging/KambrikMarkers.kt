package io.ejekta.kambrik.api.logging

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.KambrikMod


internal object KambrikMarkers {

    val Registry = mutableMapOf<String, Boolean>()

    val General = Kambrik.Logging.createMarker(KambrikMod.idOf("general"))
    val Rendering = Kambrik.Logging.createMarker(KambrikMod.idOf("rendering"))

    fun handleContainerMarkers(modId: String, states: Map<String, Boolean>) {
        for ((key, state) in states) {
            Registry[key] = state
        }
    }

}