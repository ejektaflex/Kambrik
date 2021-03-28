package io.ejekta.kambrik.internal

import kotlinx.serialization.Serializable

@Serializable
class KambrikInternalConfig {
    val markers = mutableMapOf<String, Boolean>()
}