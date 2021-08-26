package io.ejekta.kambrikx.data.server

import kotlinx.serialization.KSerializer
import net.minecraft.util.Identifier
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ServerDataProperty<T : Any>(
    val key: Identifier,
    val default: () -> T,
    val serializer: KSerializer<T>
    ) : ReadWriteProperty<Any, T> {

    init {
        ServerDataRegistrar.request(key, serializer, default())
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return ServerDataRegistrar.loadResult(key)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        ServerDataRegistrar.setResult(key, value)
    }

}