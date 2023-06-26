package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.input.KambrikKeybind
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.minecraft.client.option.KeyBinding
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

interface KambrikSharedApi {

    // Loader

    val side: BridgeSide

    // Messaging

    // * Client

    fun isOnClient(): Boolean

    fun isOnServer(): Boolean

    fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>, id: Identifier): Boolean

    fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity, msgId: Identifier)

    // * Server

    fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>, id: Identifier): Boolean

    fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M, msgId: Identifier)

    // Registration

    fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T

    // Keybinds

    fun hookKeybindUpdatesRealtime(keybind: KambrikKeybind, func: KambrikKeybind.() -> Unit)
    fun hookKeybindUpdates(keybind: KambrikKeybind, func: KambrikKeybind.() -> Unit)

}