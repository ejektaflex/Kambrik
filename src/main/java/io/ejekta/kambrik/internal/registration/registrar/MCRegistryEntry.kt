package io.ejekta.kambrik.internal.registration.registrar

import io.ejekta.kambrik.ext.register
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

data class MCRegistryEntry<T>(val registry: Registry<T>, val itemId: String, val item: T) : IRegistrar {
    override fun register(modId: String) = registry.register(Identifier(modId, itemId), item)
}