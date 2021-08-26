package io.ejekta.kambrikx.network.pakkit

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

interface ServerBoundPakkit : Pakkit {
    fun sendToServer() {

        ClientSidePacketRegistry.INSTANCE.sendToServer(getId(), write()).also {
            println("Sending packet to server: '${getId()}'. Is server set to receive the packet?: " +
             "${ClientSidePacketRegistry.INSTANCE.canServerReceive(getId())}")
        }
    }
}