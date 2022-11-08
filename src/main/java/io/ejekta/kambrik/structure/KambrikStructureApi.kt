package io.ejekta.kambrik.structure

import com.mojang.datafixers.util.Pair
import io.ejekta.kambrik.internal.mixins.StructurePoolAccessor
import net.minecraft.server.MinecraftServer
import net.minecraft.structure.pool.StructurePool
import net.minecraft.structure.pool.StructurePoolElement
import net.minecraft.structure.processor.StructureProcessorList
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.util.registry.RegistryKey




/**
 * Accessed via [Kambrik.Structure][io.ejekta.kambrik.Kambrik.Structure]
 */
class KambrikStructureApi internal constructor() {

    private val EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(
        Registry.STRUCTURE_PROCESSOR_LIST_KEY, Identifier("minecraft", "empty")
    )

    // Meant to be called from inside a ServerLifecycleEvents.SERVER_STARTING event
    fun addToStructurePool(server: MinecraftServer, nbtLocation: Identifier, poolLocation: Identifier, weight: Int = 10_000) {
        val emptyProcessorList: RegistryEntry<StructureProcessorList> =
            server.registryManager[Registry.STRUCTURE_PROCESSOR_LIST_KEY]
                .entryOf(EMPTY_PROCESSOR_LIST_KEY)

        val pool = server.registryManager.get(Registry.STRUCTURE_POOL_KEY)
            .find { it.id.toString() == poolLocation.toString() } ?: throw Exception("Cannot add to '$poolLocation' as it cannot be found!")

        val pieceList = (pool as StructurePoolAccessor).elements
        val piece = StructurePoolElement.ofProcessedSingle(nbtLocation.toString(), emptyProcessorList).apply(StructurePool.Projection.RIGID)

        val list = (pool as StructurePoolAccessor).elementCounts.toMutableList()
        list.add(Pair(piece, weight))
        (pool as StructurePoolAccessor).elementCounts = list

        repeat(weight) {
            pieceList.add(piece)
        }
    }

}