package io.ejekta.kambrik.api.serial.serializers

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.api.serial.ItemData
import io.ejekta.kambrik.internal.KambrikExperimental
import kotlinx.serialization.json.Json
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString

@KambrikExperimental
abstract class ItemDataJson<T> : ItemData<T>() {

    private val format = Json {
        serializersModule = Kambrik.Serial.DefaultSerializers
    }

    override val defaultTag: NbtElement
        get() = NbtString.of(format.encodeToString(ser, default()))

    override fun encode(value: T): NbtElement {
        return NbtString.of(format.encodeToString(ser, value))
    }

    override fun decode(nbt: NbtElement): T {
        return format.decodeFromString(ser, (nbt as NbtString).asString())
    }

}