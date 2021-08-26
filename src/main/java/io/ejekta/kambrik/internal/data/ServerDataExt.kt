package io.ejekta.kambrik.internal.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier

inline fun <reified T : Any> serverData(
    key: Identifier,
    default: T,
    serializer: KSerializer<T> = DefaultJsonFormat.serializersModule.serializer<T>(),
    noinline shouldSave: MinecraftServer.() -> Boolean = { true }
): ServerDataProperty<T> {
    return ServerDataProperty(key, default, serializer, shouldSave)
}