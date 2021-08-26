package io.ejekta.kambrik.internal.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ServerDataProperty<T : Any>(
    val key: Identifier,
    val default: T,
    val serializer: KSerializer<T>,
    val shouldSave: MinecraftServer.() -> Boolean = { true }
    ) : ReadWriteProperty<Any, T> {

    init {
        ServerDataRegistrar.request(key, serializer, default)
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return ServerDataRegistrar.loadResult(key)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        ServerDataRegistrar.setResult(key, value)
    }

}