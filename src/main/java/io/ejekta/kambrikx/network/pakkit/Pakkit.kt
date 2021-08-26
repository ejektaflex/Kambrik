package io.ejekta.kambrikx.network.pakkit

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

interface Pakkit {

    fun getId(): Identifier

    fun read(buf: PacketByteBuf)

    fun write(): PacketByteBuf

}