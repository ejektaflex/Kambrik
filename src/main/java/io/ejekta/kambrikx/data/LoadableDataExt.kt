package io.ejekta.kambrikx.data

import io.ejekta.kambrikx.data.config.ConfigDataProperty
import io.ejekta.kambrikx.data.server.ServerDataProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import kotlin.properties.ReadWriteProperty


inline fun <reified T : Any> serverData(
    key: Identifier,
    serializersModule: SerializersModule = DefaultJsonFormat.serializersModule,
    serializer: KSerializer<T> = serializersModule.serializer(),
    noinline default: () -> T,
): ReadWriteProperty<Any, T> {
    return ServerDataProperty(key, default, serializer)
}

inline fun <reified T : Any> configData(
    key: Identifier,
    serializersModule: SerializersModule = DefaultJsonFormat.serializersModule,
    serializer: KSerializer<T> = serializersModule.serializer(),
    noinline default: () -> T,
): ReadWriteProperty<Any, T> {
    return ConfigDataProperty(key, default, serializer)
}
