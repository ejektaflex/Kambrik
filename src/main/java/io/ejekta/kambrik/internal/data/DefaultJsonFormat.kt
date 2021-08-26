package io.ejekta.kambrik.internal.data

import io.ejekta.kambrik.serial.serializers.IdentitySer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.util.Identifier

val DefaultJsonFormat = Json {
    prettyPrint = true
    serializersModule = SerializersModule {
        contextual(Identifier::class, IdentitySer)
    }
}