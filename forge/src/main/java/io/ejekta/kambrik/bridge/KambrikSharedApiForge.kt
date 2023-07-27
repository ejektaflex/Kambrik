package io.ejekta.kambrik.bridge

import com.google.auto.service.AutoService
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.input.KambrikKeybind
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.Supplier


class KambrikSharedApiForge : KambrikSharedApi {

    init {
        Kambrik.Logger.debug("Kambrik Shared API (Forge) Initialized.")
    }

    override val side: BridgeSide
        get() = BridgeSide.FORGE

    // Event methods

    override fun isOnClient(): Boolean {
        return FMLEnvironment.dist == Dist.CLIENT
    }

    override fun isOnServer(): Boolean {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER
    }

    // Messaging

    private val clientMsgMap = mutableMapOf<INetworkLink<*>, SimpleChannel>()
    private val serverMsgMap = mutableMapOf<INetworkLink<*>, SimpleChannel>()

    abstract class ForgeMsgWrapper<MSG : Any>(open val link: INetworkLink<MSG>, val workFunc: NetworkEvent.Context.(msg: MSG) -> Unit) {
        fun writer(msg: MSG, buff: PacketByteBuf) {
            buff.writeString(link.serializePacket(msg))
        }
        fun reader(buff: PacketByteBuf): MSG {
            return link.deserializePacket(buff.readString())
        }
        fun handler(msg: MSG, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().run {
                enqueueWork {
                    workFunc(msg)
                }
                packetHandled = true
            }
        }
    }

    private fun createDummyChannel(id: Identifier): SimpleChannel {
        val version = "1"
        return NetworkRegistry.newSimpleChannel(
            id, { version },
            version::equals,
            version::equals
        )
    }

    class ForgeServerMsgWrapper<MSG : ServerMsg>(override val link: INetworkLink<MSG>) : ForgeMsgWrapper<MSG>(link, { msg ->
        msg.onServerReceived(ServerMsg.MsgContext(sender!!))
    })

    class ForgeClientMsgWrapper<MSG : ClientMsg>(override val link: INetworkLink<MSG>) : ForgeMsgWrapper<MSG>(link, { msg ->
        msg.onClientReceived(ClientMsg.MsgContext(MinecraftClient.getInstance()))
    })

    private fun <M : Any> registerGenericMsg(link: INetworkLink<M>, regMap: MutableMap<INetworkLink<*>, SimpleChannel>, wrapperSupplier: (link: INetworkLink<M>) -> ForgeMsgWrapper<M>): SimpleChannel {
        val dummy = createDummyChannel(link.id)
        val wrapper = wrapperSupplier(link)
        regMap[link] = dummy
        Kambrik.Logger.debug("Registered network link ${link.id}")
        dummy.registerMessage(1, link.kClass.java, wrapper::writer, wrapper::reader, wrapper::handler)
        return dummy
    }

    override fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>): Boolean {
        registerGenericMsg(link, serverMsgMap) { ForgeServerMsgWrapper(link) }
        return true
    }

    override fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>): Boolean {
        registerGenericMsg(link, clientMsgMap) { ForgeClientMsgWrapper(link) }
        return true
    }

    override fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M, msgId: Identifier) {
        val channel = serverMsgMap[link] ?: throw Exception("Server Link not found in Channel Mapping for: ${link.id}, Msg ID: $msgId")
        channel.sendToServer(msg)
    }

    override fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity, msgId: Identifier) {
        val channel = clientMsgMap[link] ?: throw Exception("Client Link not found in Channel Mapping for: ${link.id}, Msg ID: $msgId")
        channel.sendTo(msg, player.networkHandler.connection, NetworkDirection.PLAY_TO_CLIENT)
    }

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        reg.register(Kambrik.idOf(thingId), obj)
        return obj
    }

}