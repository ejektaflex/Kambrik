package io.ejekta.kambrik.ext

import net.fabricmc.loader.api.metadata.CustomValue

fun CustomValue.CvObject.toMap(): Map<String, CustomValue> {
    return map { it.key to it.value }.toMap()
}