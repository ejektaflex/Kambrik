package io.ejekta.kambrik.internal

import kotlinx.serialization.Serializable

@Serializable
internal class KambrikInternalConfig {
    val markers = mutableMapOf<String, Boolean>()
}