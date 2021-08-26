package io.ejekta.kambrikx.data.config

import kotlinx.serialization.KSerializer
import net.minecraft.util.Identifier
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ConfigDataProperty<T : Any>(
    val key: Identifier,
    val default: () -> T,
    val serializer: KSerializer<T>
    ) : ReadWriteProperty<Any, T> {

    init {
        ConfigDataRegistrar.request(key, serializer, default())
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return ConfigDataRegistrar.loadResult(key)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        ConfigDataRegistrar.setResult(key, value)
    }

}