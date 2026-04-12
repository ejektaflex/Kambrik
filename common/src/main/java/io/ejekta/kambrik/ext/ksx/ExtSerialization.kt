package io.ejekta.kambrik.ext.ksx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.minecraft.nbt.StringTag

fun <T> Json.encodeToStringTag(serializer: KSerializer<T>, value: T): StringTag {
    return StringTag.valueOf(encodeToString(serializer, value))
}

fun <T> Json.decodeFromStringTag(serializer: KSerializer<T>, nbtString: StringTag): T {
    return decodeFromString(serializer, nbtString.asString().orElseThrow())
}


