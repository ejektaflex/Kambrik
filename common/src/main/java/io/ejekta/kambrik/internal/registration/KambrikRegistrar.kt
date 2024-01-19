package io.ejekta.kambrik.internal.registration

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object KambrikRegistrar {

    data class RegistrationEntry<T>(val registry: Registry<T>, val itemId: String, val item: Lazy<T>) {
        fun register(modId: String) {
            Kambrik.Logger.debug("Registering item: ${modId}:${itemId}")
            registry.register(Identifier(modId, itemId), item.value)
        }
    }

    data class ModRegistrar(val requestor: KambrikAutoRegistrar, val content: MutableList<RegistrationEntry<*>> = mutableListOf())

    private val registrars = mutableMapOf<KambrikAutoRegistrar, ModRegistrar>()

    operator fun get(requester: KambrikAutoRegistrar): ModRegistrar {
        return registrars.getOrPut(requester) { ModRegistrar(requester) }
    }

    fun <T> register(requester: KambrikAutoRegistrar, reg: Registry<T>, itemId: String, obj: Lazy<T>): Lazy<T> {
        Kambrik.Logger.debug("Kambrik registering '${requester::class.qualifiedName} for '$itemId' for auto-registration")
        this[requester].content.add(RegistrationEntry(reg, itemId, obj))
        return obj
    }

    fun doRegistrationsFor(modId: String) {
        registrars.filter { it.key.getId() == modId }.forEach { (_, items) ->
            for (item in items.content) {
                item.register(modId)
            }
        }
    }


}