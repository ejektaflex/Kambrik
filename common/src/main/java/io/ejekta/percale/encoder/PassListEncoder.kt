package io.ejekta.percale.encoder

import com.mojang.serialization.DynamicOps
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class PassListEncoder<T>(override val ops: DynamicOps<T>, serialMod: SerializersModule) : PassEncoder<T>(ops, serialMod) {

    override fun encodeFunc(func: () -> T) {
        push(func())
    }

    override fun push(result: T) {
        listBuilder.add(result)
    }

    private var lastIndex = -1
    private var currentIndex = -1

    private val listBuilder = mutableListOf<T>()
    private val nestedEncoders = mutableListOf<PassEncoder<T>>()

    override val serializersModule = EmptySerializersModule()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (lastIndex == currentIndex) {
            return this
        }
        val nestedEncoder = pickEncoder(descriptor, ops, serializersModule)
        nestedEncoders.add(nestedEncoder)
        return nestedEncoder
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        for (nestedEncoder in nestedEncoders) {
            listBuilder.add(nestedEncoder.getResult())
        }
    }

    override fun getResult(): T {
        return ops.createList(listBuilder.stream())
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        lastIndex = currentIndex
        currentIndex = index
        return true
    }
}