package io.ejekta.kambrikx.data

import io.ejekta.kambrikx.data.server.ServerDataRegistrar
import kotlinx.serialization.KSerializer
import net.minecraft.util.Identifier
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class DataProperty<T : Any>(
    val key: Identifier,
    val default: () -> T,
    val serializer: KSerializer<T>,
    private val registrar: LoadableDataRegistrar
) : ReadWriteProperty<Any, T> {

    init {
        println("DataProperty requesting: $serializer")
        registrar.request(key, serializer, default())
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return registrar.loadResult(key)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        registrar.setResult(key, value)
    }

}