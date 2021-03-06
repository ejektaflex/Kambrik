package io.ejekta.kambrik.api.logging

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.KambrikMod
import org.apache.logging.log4j.Marker


internal object KambrikMarkers {

    val Registry = mutableMapOf<String, Boolean>()

    val General = createIdMarker("general")
    val Rendering = createIdMarker("rendering")

    private fun createIdMarker(name: String): Marker {
        return Kambrik.Logging.createMarker(KambrikMod.idOf(name))
    }

    internal fun handleContainerMarkers(states: Map<String, Boolean>) {
        for ((key, state) in states) {
            Registry[key] = state
        }
    }

}