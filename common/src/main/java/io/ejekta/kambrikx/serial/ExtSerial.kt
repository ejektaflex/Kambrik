package io.ejekta.kambrikx.serial

import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.KambrikMsg
import kotlinx.serialization.KSerializer
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec

fun <M : KambrikMsg> KSerializer<M>.toSimplePacketCodec(): PacketCodec<RegistryByteBuf, M> {
    val json = INetworkLink.defaultJson
    return PacketCodec.of(
        { value, buf -> buf.writeString(json.encodeToString(this, value)) },
        { json.decodeFromString(this, it.readString()) }
    )
}