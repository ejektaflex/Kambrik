package io.ejekta.kambrik.api.network

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.KSerializer
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier

interface INetworkLink<M> {

    val id: Identifier
    val ser: KSerializer<M>

    fun register(): Boolean

    fun serializePacket(m: M): NbtElement {
        return NbtFormat.Default.encodeToTag(ser, m)
    }

    fun deserializePacket(tag: NbtElement): M {
        return NbtFormat.Default.decodeFromTag(ser, tag)
    }

}