package io.ejekta.kambrikx.data

import io.ejekta.kambrikx.data.server.ServerDataProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier

inline fun <reified T : Any> serverData(
    key: Identifier,
    serializer: KSerializer<T> = DefaultJsonFormat.serializersModule.serializer(),
    noinline default: () -> T,
): ServerDataProperty<T> {
    return ServerDataProperty(key, default, serializer)
}