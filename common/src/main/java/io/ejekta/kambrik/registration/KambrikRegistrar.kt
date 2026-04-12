package io.ejekta.kambrik.registration

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.Identifier
import io.ejekta.kambrik.ext.register
import net.minecraft.core.Registry

object KambrikRegistrar {

    data class RegistrationEntry<T : Any>(val registry: Registry<T>, val itemId: String, val item: Lazy<T>) {
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

    fun <T : Any> register(requester: KambrikAutoRegistrar, reg: Registry<T>, itemId: String, obj: Lazy<T>): Lazy<T> {
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

    fun doRegistrationsFor(requester: KambrikAutoRegistrar) {
        this[requester].content.forEach { item ->
            item.register(requester.getId())
        }
    }


}