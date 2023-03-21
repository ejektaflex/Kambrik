package io.ejekta.kambrik.logging

import io.ejekta.kambrik.Kambrik
import org.apache.logging.log4j.Marker


internal object KambrikMarkers {

    val Registry = mutableMapOf<String, Boolean>()

    val General = createIdMarker("general")
    val Rendering = createIdMarker("rendering")

    private fun createIdMarker(name: String): Marker {
        return Kambrik.Logging.createMarker(Kambrik.idOf(name))
    }

    internal fun handleContainerMarkers(states: Map<String, Boolean>) {
        for ((key, state) in states) {
            Registry[key] = state
        }
    }

}