package io.ejekta.kambrikx.data

import kotlinx.serialization.KSerializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class DataProperty<T : Any>(
    val key: String,
    val default: () -> T,
    val serializer: KSerializer<T>,
    private val dataFile: DataFile
) : ReadWriteProperty<Any, T> {

    init {
        println("DataProperty requesting: $serializer")
        dataFile.request(key, serializer, default())
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return dataFile.loadResult(key)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        dataFile.setResult(key, value)
    }

}