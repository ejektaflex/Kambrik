package io.ejekta.kambrik.ext.jvm

import io.ejekta.kambrik.ext.internal.assured
import java.nio.file.Path

fun Path.ensured(folderName: String): Path {
    return resolve(folderName).assured
}

