package percale.decoder

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule

class PassMapDecoder<T>(override val ops: DynamicOps<T>, private val input: T, level: Int) : PassDecoder<T>(ops, level) {

    override val serializersModule = EmptySerializersModule()

    private val inputMap =
        ops.getMap(input).result().get()
    // Flatten map into a 1d array
    private val entries = inputMap.entries().map { listOf(it.first, it.second) }.toList().flatten()
    private val keyCount = entries.size
    private var currentIndex = -1

    override val currentValue: T?
        get() {
            return entries[currentIndex]
        }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (currentIndex < 0) {
            return this
        }
        val pickedDecoder = pickDecoder(descriptor, ops, currentValue!!, level)
        return pickedDecoder
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        currentIndex += 1
        return if (currentIndex < keyCount) currentIndex else CompositeDecoder.DECODE_DONE.also { println("(done)") }
    }

    override fun <V> decodeFunc(func: () -> DataResult<V>): V {
        //debug("Decoding $currentIndex - $currentKey - $currentValue")
        val dataResult = func()
        return dataResult.orThrow
    }

}