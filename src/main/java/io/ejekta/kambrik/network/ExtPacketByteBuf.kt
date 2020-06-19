package io.ejekta.kambrik.network

import net.minecraft.network.PacketByteBuf

fun PacketByteBuf.writeEnum(enum: Enum<*>) {
    writeInt(enum.ordinal)
}

inline fun <reified T : Enum<T>> PacketByteBuf.readEnum(): T {
    return enumValues<T>()[readInt()]
}
