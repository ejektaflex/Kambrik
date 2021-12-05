package io.ejekta.kambrik.internal.registration

import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.KambrikMod
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

internal object KambrikRegistrar {

    data class RegistrationEntry<T>(val registry: Registry<T>, val itemId: String, val item: T) {
        fun register(modId: String) = registry.register(Identifier(modId, itemId), item)
    }

    data class ModRegistrar(val requestor: KambrikAutoRegistrar, val content: MutableList<RegistrationEntry<*>> = mutableListOf())

    private val registrars = mutableMapOf<KambrikAutoRegistrar, ModRegistrar>()

    operator fun get(requester: KambrikAutoRegistrar): ModRegistrar {
        return registrars.getOrPut(requester) { ModRegistrar(requester) }
    }

    fun <T> register(requester: KambrikAutoRegistrar, reg: Registry<T>, itemId: String, obj: T): T {
        KambrikMod.Logger.debug("Kambrik registering '${requester::class.qualifiedName} for $itemId' for autoregistration")
        this[requester].content.add(RegistrationEntry(reg, itemId, obj))
        return obj
    }

    fun doRegistrationFor(container: EntrypointContainer<KambrikMarker>) {
        KambrikMod.Logger.debug("Kambrik doing real registration for mod ${container.provider.metadata.id}")
        this[container.entrypoint as? KambrikAutoRegistrar ?: return].apply {
            requestor.mainRegister()
            content.forEach { entry ->
                entry.register(container.provider.metadata.id)
            }
        }
    }

    fun doClientRegistrationFor(container: EntrypointContainer<KambrikMarker>) {
        KambrikMod.Logger.debug("Kambrik doing real client registration for mod ${container.provider.metadata.id}")
        this[container.entrypoint as? KambrikAutoRegistrar ?: return].apply {
            requestor.clientRegister()
        }
    }

    fun doServerRegistrationFor(container: EntrypointContainer<KambrikMarker>) {
        KambrikMod.Logger.debug("Kambrik doing real server registration for mod ${container.provider.metadata.id}")
        this[container.entrypoint as? KambrikAutoRegistrar ?: return].apply {
            requestor.serverRegister()
        }
    }
}