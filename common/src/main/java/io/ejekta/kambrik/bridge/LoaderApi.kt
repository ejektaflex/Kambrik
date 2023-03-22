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

interface LoaderApi {

    // Loader

    val side: BridgeSide

    // Input

    fun registerKeybind(kb: KeyBinding)

    fun hookKeybindUpdates(kambrikKeybind: KambrikKeybind, func: KambrikKeybind.() -> Unit)

    fun hookKeybindUpdatesRealtime(kambrikKeybind: KambrikKeybind, func: KambrikKeybind.() -> Unit)

    // Messaging

    // * Client

    fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>, id: Identifier): Boolean

    fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity, msgId: Identifier)

    // * Server

    fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>, id: Identifier): Boolean

    fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M, msgId: Identifier)

    // Registration

    fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T

}