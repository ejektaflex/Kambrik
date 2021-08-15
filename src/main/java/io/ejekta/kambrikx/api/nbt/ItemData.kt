package io.ejekta.kambrikx.api.nbt

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier

abstract class ItemData<T> {

    abstract val identifier: Identifier

    abstract val ser: KSerializer<T>

    abstract val default: () -> T

    private val defaultTag: NbtElement
        get() = format.encodeToTag(ser, default())

    open val format: NbtFormat = NbtFormat.Default

    fun of(stack: ItemStack) = get(stack)

    private fun getSubtag(stack: ItemStack): NbtElement {
        stack.orCreateNbt.apply {
            val key = identifier.toString()
            return if (key in this) {
                get(key)
            } else {
                val def = defaultTag
                put(key, def)
                def
            }!!
        }
    }

    private fun setSubtag(stack: ItemStack, tag: NbtElement) {
        stack.orCreateNbt.apply {
            put(identifier.toString(), tag)
        }
    }

    operator fun get(stack: ItemStack): T {
        // We should not be grabbing NBT if it's an empty item
        if (stack.item == null) {
            throw error("Item is an empty itemstack, so NBT data cannot be trusted")
        }

        val tag = getSubtag(stack)

        return try {
            format.decodeFromTag(ser, tag)
        } catch (e: SerializationException) {
            Kambrik.Logger.error("Failed to decode leaf '$identifier' in stack $stack (type: ${stack.item::class.simpleName})")
            default().also { set(stack, it) }
        }
    }

    operator fun set(stack: ItemStack, value: T) {
        val tag = format.encodeToTag(ser, value)
        setSubtag(stack, tag)
    }

    fun edit(stack: ItemStack, func: T.() -> Unit) {
        get(stack).apply(func).also { set(stack, it) }
    }

    fun editIf(stack: ItemStack, func: T.() -> Boolean) {
        get(stack).apply {
            val result = func()
            if (result) {
                set(stack, this)
            }
        }
    }

}