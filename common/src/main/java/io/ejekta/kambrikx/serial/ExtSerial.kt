package io.ejekta.kambrikx.serial

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.KSerializer
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

//*
fun <M : KambrikMsg> KSerializer<M>.toSimplePacketCodec(): StreamCodec<RegistryFriendlyByteBuf, M> {
    val json = Kambrik.Serial.networkingFormat()
    return StreamCodec.ofMember(
        { value, buf ->
            buf.writeUtf(json.encodeToString(this, value))
        },
        { json.decodeFromString(this, it.readUtf()) }
    )
}