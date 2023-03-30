package io.ejekta.kambrikx.data

import kotlinx.serialization.KSerializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class DataProperty<T : Any>(
    var key: String?,
    val default: () -> T,
    val serializer: KSerializer<T>,
    private val dataFile: DataFile
) : ReadWriteProperty<Any, T> {

    private fun getKeyValue(property: KProperty<*>): String {
        return key ?: property.name
    }

    private fun validateRequest(property: KProperty<*>) {
        val propKey = getKeyValue(property)
        if (!dataFile.hasRequested(propKey)) {
            dataFile.request(propKey, serializer, default())
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val propKey = getKeyValue(property)
        validateRequest(property)
        return dataFile.loadResult(propKey)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        validateRequest(property)
        dataFile.setResult(getKeyValue(property), value)
    }

}