package io.ejekta.kambrik.internal.registration.registrar

internal class CustomRegistrarEntry(private val block: (String) -> Unit) : IRegistrar {
    override fun register(modId: String) = block(modId)
}