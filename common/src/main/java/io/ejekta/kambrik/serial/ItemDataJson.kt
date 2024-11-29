package io.ejekta.kambrik.serial

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.ksx.decodeFromStringTag
import io.ejekta.kambrik.ext.ksx.encodeToStringTag
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.Tag

//abstract class ItemDataJson<T> : ItemData<T>() {
//
//    override val serializersModule: SerializersModule
//        get() = Kambrik.Serial.DefaultSerializers
//
//    private val format = Json {
//        this.serializersModule = this@ItemDataJson.serializersModule
//    }
//
//    override val defaultTag: Tag
//        get() = format.encodeToStringTag(ser, default())
//
//    override fun encode(value: T): Tag {
//        return format.encodeToStringTag(ser, value)
//    }
//
//    override fun decode(nbt: Tag): T {
//        return format.decodeFromStringTag(ser, nbt as NbtString)
//    }
//
//}