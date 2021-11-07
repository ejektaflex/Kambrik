package io.ejekta.kambrikx.data

import kotlinx.serialization.KSerializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class DataProperty<T : Any>(
    var key: String,
    val default: () -> T,
    val serializer: KSerializer<T>,
    private val dataFile: DataFile
) : ReadWriteProperty<Any, T> {

    fun getKeyValue(property: KProperty<*>): String {
        return key ?: property.name
    }

    init {
        println("DataProperty requesting: $serializer")
        dataFile.request(key, serializer, default())
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (key.isBlank()) {
            key = property.name
        }
        println("Getting prop: ${property.name} with key $key")
        return dataFile.loadResult(key)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        dataFile.setResult(key, value)
    }

}