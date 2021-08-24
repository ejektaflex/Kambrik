package io.ejekta.kambrik.structure

import io.ejekta.kambrik.internal.mixins.StructurePoolAccessor
import net.minecraft.server.MinecraftServer
import net.minecraft.structure.pool.StructurePool
import net.minecraft.structure.pool.StructurePoolElement
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/**
 * Accessed via [Kambrik.Structure][io.ejekta.kambrik.Kambrik.Structure]
 */
class KambrikStructureApi internal constructor() {

    // Meant to be called from inside of a ServerLifecycleEvents.SERVER_STARTING event
    fun addToStructurePool(server: MinecraftServer, nbtLocation: Identifier, poolLocation: Identifier, weight: Int = 10_000) {
        val pool = server.registryManager.get(Registry.STRUCTURE_POOL_KEY).entries
            .find { it.key.value.toString() == poolLocation.toString() }?.value ?: throw Exception("Cannot add to '$poolLocation' as it cannot be found!")
        val pieceList = (pool as StructurePoolAccessor).kambrik_getStructureElements()
        val piece = StructurePoolElement.ofSingle(nbtLocation.toString()).apply(StructurePool.Projection.RIGID)
        repeat(weight) {
            pieceList.add(piece)
        }
    }

}