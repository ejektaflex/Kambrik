package io.ejekta.kambrikx.serial

import io.ejekta.kambrik.serial.KambrikSerialApi
import io.ejekta.kambrik.internal.KambrikExperimental
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.*

@KambrikExperimental
val KambrikSerialApi.NbtSerializers: SerializersModule
    get() = SerializersModule {

        // Basic Tags
        contextual(NbtElement::class, DynTagSerializer)
        contextual(NbtCompound::class, DynTagSerializer())
        contextual(NbtInt::class, DynTagSerializer())
        contextual(NbtString::class, DynTagSerializer())
        contextual(NbtDouble::class, DynTagSerializer())
        contextual(NbtByte::class, DynTagSerializer())
        contextual(NbtFloat::class, DynTagSerializer())
        contextual(NbtLong::class, DynTagSerializer())
        contextual(NbtShort::class, DynTagSerializer())

        // Complex Tags
        contextual(NbtLongArray::class, DynTagSerializer())
        contextual(NbtIntArray::class, DynTagSerializer())
        contextual(NbtList::class, DynTagSerializer())
    }