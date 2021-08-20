package io.ejekta.kambrik.api.serial

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.ksx.decodeFromStringTag
import io.ejekta.kambrik.ext.ksx.encodeToStringTag
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString

abstract class ItemDataJson<T> : ItemData<T>() {

    override val serializersModule: SerializersModule
        get() = Kambrik.Serial.DefaultSerializers

    private val format = Json {
        this.serializersModule = this@ItemDataJson.serializersModule
    }

    override val defaultTag: NbtElement
        get() = format.encodeToStringTag(ser, default())

    override fun encode(value: T): NbtElement {
        return format.encodeToStringTag(ser, value)
    }

    override fun decode(nbt: NbtElement): T {
        return format.decodeFromStringTag(ser, nbt as NbtString)
    }

}