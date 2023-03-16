package io.ejekta.kambrik.structure

import com.mojang.datafixers.util.Pair
import io.ejekta.kambrik.internal.mixins.StructurePoolAccessor
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.MinecraftServer
import net.minecraft.structure.pool.StructurePool
import net.minecraft.structure.pool.StructurePoolElement
import net.minecraft.structure.processor.StructureProcessorList
import net.minecraft.util.Identifier




/**
 * Accessed via [Kambrik.Structure][io.ejekta.kambrik.Kambrik.Structure]
 */
class KambrikStructureApi internal constructor() {

    private val EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(
        RegistryKeys.PROCESSOR_LIST, Identifier("minecraft", "empty")
    )



    // Meant to be called from inside a ServerLifecycleEvents.SERVER_STARTING event
    fun addToStructurePool(server: MinecraftServer, nbtLocation: Identifier, poolLocation: Identifier, weight: Int = 10_000) {
        val emptyProcessorList: RegistryEntry<StructureProcessorList> =
            server.registryManager[RegistryKeys.PROCESSOR_LIST]
                .entryOf(EMPTY_PROCESSOR_LIST_KEY)

        val poolGrabber = server.registryManager[RegistryKeys.TEMPLATE_POOL].getOrEmpty(poolLocation)

        if (poolGrabber.isEmpty) {
            throw Exception("Cannot add to '$poolLocation' as it cannot be found!")
        }

        val pool = poolGrabber.get()

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