package io.ejekta.kambrik.api.network

import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.KSerializer
import net.minecraft.util.Identifier

data class PacketInfo<S>(
    override val id: Identifier,
    override val serial: KSerializer<S>,
    override val format: NbtFormat = NbtFormat.Default
) : IPacketInfo<S> {

    constructor(idPair: Pair<Identifier, KSerializer<S>>) : this(idPair.first, idPair.second)

}
