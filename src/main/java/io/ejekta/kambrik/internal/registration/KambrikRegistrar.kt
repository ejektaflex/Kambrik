package io.ejekta.kambrik.internal.registration

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.KambrikMod
import io.ejekta.kambrik.internal.registration.registrar.IRegistrar
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.entrypoint.EntrypointContainer

internal object KambrikRegistrar {

    class ModRegistrarEnvironments(requester: KambrikAutoRegistrar) {
        val main = ModRegistrar(requester)
        val client = ModRegistrar(requester)
        val server = ModRegistrar(requester)
    }
    data class ModRegistrar(val requestor: KambrikAutoRegistrar, val content: MutableList<IRegistrar> = mutableListOf())

    private val registrars = mutableMapOf<KambrikAutoRegistrar, ModRegistrarEnvironments>()

    operator fun get(requester: KambrikAutoRegistrar): ModRegistrarEnvironments {
        return registrars.getOrPut(requester) { ModRegistrarEnvironments(requester) }
    }

    fun register(requester: KambrikAutoRegistrar, registrar: IRegistrar, envType: EnvType? = null) {
        KambrikMod.Logger.debug("Kambrik registering '${requester::class.qualifiedName} for $registrar' for autoregistration")

        when (envType) {
            EnvType.CLIENT -> this[requester].client.content.add(registrar)
            EnvType.SERVER -> this[requester].server.content.add(registrar)
            null -> this[requester].main.content.add(registrar)
        }
    }

    fun doMainRegistrationFor(container: EntrypointContainer<KambrikMarker>) {
        KambrikMod.Logger.debug("Kambrik doing real registration for mod ${container.provider.metadata.id}")
        this[container.entrypoint as? KambrikAutoRegistrar ?: return].main.apply {
            requestor.mainRegister()
            content.forEach { entry ->
                entry.register(container.provider.metadata.id)
            }
        }
    }

    fun doClientRegistrationFor(container: EntrypointContainer<KambrikMarker>) {
        KambrikMod.Logger.debug("Kambrik doing real client registration for mod ${container.provider.metadata.id}")
        this[container.entrypoint as? KambrikAutoRegistrar ?: return].client.apply {
            requestor.clientRegister()
            content.forEach { entry ->
                entry.register(container.provider.metadata.id)
            }
        }
    }

    fun doServerRegistrationFor(container: EntrypointContainer<KambrikMarker>) {
        KambrikMod.Logger.debug("Kambrik doing real server registration for mod ${container.provider.metadata.id}")
        this[container.entrypoint as? KambrikAutoRegistrar ?: return].server.apply {
            requestor.serverRegister()
            content.forEach { entry ->
                entry.register(container.provider.metadata.id)
            }
        }
    }
}