package io.ejekta.kambrik.ext.ksx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.minecraft.nbt.NbtString

fun <T> Json.encodeToStringTag(serializer: KSerializer<T>, value: T): NbtString {
    return NbtString.of(encodeToString(serializer, value))
}

fun <T> Json.decodeFromStringTag(serializer: KSerializer<T>, nbtString: NbtString): T {
    return decodeFromString(serializer, nbtString.asString())
}
