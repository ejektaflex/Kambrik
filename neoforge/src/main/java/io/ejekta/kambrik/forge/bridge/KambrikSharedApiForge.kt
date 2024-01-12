package io.ejekta.kambrik.forge.bridge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.ClientNetworkLink
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.network.INetworkDirection
import net.neoforged.neoforge.network.LoginNetworkDirection
import net.neoforged.neoforge.network.NetworkEvent
import net.neoforged.neoforge.network.NetworkRegistry
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.PacketDistributor.PacketTarget
import net.neoforged.neoforge.network.PlayNetworkDirection
import net.neoforged.neoforge.network.simple.SimpleChannel
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
        return NetworkRegistry.ChannelBuilder.named(id).simpleChannel() // .acceptedVersions(Channel.VersionTest { status, i -> true })
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

        val netDir = if (link is ClientNetworkLink) PlayNetworkDirection.PLAY_TO_CLIENT else PlayNetworkDirection.PLAY_TO_SERVER
        return dummy.apply {
            messageBuilder(link.kClass.java, 1, netDir)
                .encoder(wrapper::writer)
                .decoder(wrapper::reader)
                .consumerMainThread { t, u -> wrapper.handler(t) { u } }
                .add()
        }
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
        channel.sendTo(msg, player.networkHandler.connection, PlayNetworkDirection.PLAY_TO_CLIENT)
    }

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        reg.register(Kambrik.idOf(thingId), obj)
        return obj
    }

}