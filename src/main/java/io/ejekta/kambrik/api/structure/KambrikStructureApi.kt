package io.ejekta.kambrik.api.structure

import io.ejekta.kambrik.mixins.StructurePoolAccessor
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
        val pool = server.registryManager.get(Registry.TEMPLATE_POOL_WORLDGEN).entries
            .find { it.key.value.toString() == poolLocation.toString() }?.value ?: throw Exception("Cannot add to '$poolLocation' as it cannot be found!")
        val pieceList = (pool as StructurePoolAccessor).kambrik_getStructureElements()
        val piece = StructurePoolElement.method_30434(nbtLocation.toString()).apply(StructurePool.Projection.RIGID)
        repeat(weight) {
            pieceList.add(piece)
        }
    }

}