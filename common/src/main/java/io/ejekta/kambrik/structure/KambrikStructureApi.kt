package io.ejekta.kambrik.structure

import com.mojang.datafixers.util.Pair
import io.ejekta.kambrik.ext.Identifier
import io.ejekta.kambrik.internal.mixins.StructurePoolAccessor
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList
import kotlin.jvm.optionals.getOrNull

/**
 * Accessed via [Kambrik.Structure][io.ejekta.kambrik.Kambrik.Structure]
 */
class KambrikStructureApi internal constructor() {

    private val EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST, Identifier("minecraft", "empty"))

    // Meant to be called from inside a ServerLifecycleEvents.SERVER_STARTING event
    fun addToStructurePool(server: MinecraftServer, nbtLocation: ResourceLocation, poolLocation: ResourceLocation, processorLocation: ResourceLocation, weight: Int = 10_000) {
        if (weight == 0) {
            return
        }

        val emptyProcessorList: Holder.Reference<StructureProcessorList> =
            server.registryAccess().registry(Registries.PROCESSOR_LIST).get().getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY)

        val OUR_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST, processorLocation)

        val ourProcessorList: Holder.Reference<StructureProcessorList> =
            server.registryAccess().registry(Registries.PROCESSOR_LIST).get()
                .getHolder(OUR_PROCESSOR_LIST_KEY).getOrNull() ?: emptyProcessorList

        val poolGrabber = server.registryAccess().registry(Registries.TEMPLATE_POOL).get().getOptional(poolLocation)

        if (!poolGrabber.isPresent) {
            throw Exception("Cannot add to '$poolLocation' as it cannot be found!")
        }

        val pool = poolGrabber.get()

        val pieceList = (pool as StructurePoolAccessor).elements
        val piece = StructurePoolElement.single(nbtLocation.toString(), ourProcessorList).apply(
            StructureTemplatePool.Projection.RIGID)

        val list = (pool as StructurePoolAccessor).elementCounts.toMutableList()
        list.add(Pair(piece, weight))
        (pool as StructurePoolAccessor).elementCounts = list

        repeat(weight) {
            pieceList.add(piece)
        }
    }

}