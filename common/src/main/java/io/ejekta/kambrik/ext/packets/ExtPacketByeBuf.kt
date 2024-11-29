package io.ejekta.kambrik.ext.packets

import net.minecraft.network.FriendlyByteBuf

fun FriendlyByteBuf.writeEnumKambrik(enum: Enum<*>) {
    writeInt(enum.ordinal)
}

inline fun <reified T : Enum<T>> FriendlyByteBuf.readEnumKambrik(): T {
    return enumValues<T>()[readInt()]
}