package io.ejekta.kambrik.bridge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.input.KambrikKeybind
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.minecraft.client.option.KeyBinding
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class LoaderApiForge : LoaderApi {
    override val side: BridgeSide
        get() = BridgeSide.FORGE

    // Event methods

    val keysToRegister = mutableListOf<KeyBinding>()

    @SubscribeEvent
    fun registerKeys(evt: RegisterKeyMappingsEvent) {
        for (key in keysToRegister) {
            evt.register(key)
        }
    }

    override fun registerKeybind(kb: KeyBinding) {
        keysToRegister.add(kb)
    }

    override fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>, id: Identifier): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    override fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>, id: Identifier): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    override fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M, msgId: Identifier) {
        //TODO("Not yet implemented")
    }

    override fun <M : ClientMsg> sendMsgToClient(
        link: INetworkLink<M>,
        msg: M,
        player: ServerPlayerEntity,
        msgId: Identifier
    ) {
        //TODO("Not yet implemented")
    }

    override fun hookKeybindUpdatesRealtime(kambrikKeybind: KambrikKeybind, func: KambrikKeybind.() -> Unit) {
        //TODO("Not yet implemented")
    }

    override fun hookKeybindUpdates(kambrikKeybind: KambrikKeybind, func: KambrikKeybind.() -> Unit) {
        //TODO("Not yet implemented")
    }

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        reg.register(Kambrik.idOf(thingId), obj)
        return obj
    }

}