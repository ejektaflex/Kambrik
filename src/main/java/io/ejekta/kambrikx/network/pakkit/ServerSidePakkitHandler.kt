package io.ejekta.kambrikx.network.pakkit

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry

interface ServerSidePakkitHandler : PakkitHandler {
    fun registerC2S() {
        ServerSidePacketRegistry.INSTANCE.register(getId(), ::run).also {
            println("Registered packet on ServerSide: ${getId()}")
        }
    }
}