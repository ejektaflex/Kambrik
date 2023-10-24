package io.ejekta.kambrik.fabric.ext.fapi

import net.fabricmc.loader.api.metadata.CustomValue

fun CustomValue.CvObject.toMap(): Map<String, CustomValue> {
    return associate { it.key to it.value }
}