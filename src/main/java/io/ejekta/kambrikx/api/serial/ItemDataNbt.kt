package io.ejekta.kambrikx.api.serial

import io.ejekta.kambrik.api.serial.ItemData
import io.ejekta.kambrik.internal.KambrikExperimental
import net.minecraft.nbt.NbtElement

@KambrikExperimental
abstract class ItemDataNbt<T> : ItemData<T>() {

    override val defaultTag: NbtElement
        get() = format.encodeToTag(ser, default())

    open val format: NbtFormat = NbtFormat.Default

    override fun encode(value: T): NbtElement {
        return format.encodeToTag(ser, value)
    }

    override fun decode(nbt: NbtElement): T {
        return format.decodeFromTag(ser, nbt)
    }

}