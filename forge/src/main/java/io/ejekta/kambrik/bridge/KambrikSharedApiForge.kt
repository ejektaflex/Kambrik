package io.ejekta.kambrik.bridge

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
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.Supplier


class KambrikSharedApiForge : KambrikSharedApi {
    override val side: BridgeSide
        get() = BridgeSide.FORGE

    // Event methods

    // Messaging

    val channelMap = mutableMapOf<INetworkLink<*>, SimpleChannel>()

    abstract class ForgeMsgWrapper<MSG : Any>(open val link: INetworkLink<MSG>) {
        fun writer(msg: MSG, buff: PacketByteBuf) {
            buff.writeString(link.serializePacket(msg))
        }
        fun reader(buff: PacketByteBuf): MSG {
            return link.deserializePacket(buff.readString())
        }
        abstract fun handler(msg: MSG, ctx: Supplier<NetworkEvent.Context>)
    }

    fun createDummyChannel(id: Identifier): SimpleChannel {
        val version = "1"
        return NetworkRegistry.newSimpleChannel(
            id, { version },
            version::equals,
            version::equals
        )
    }

    class ForgeServerMsgWrapper<MSG : ServerMsg>(override val link: INetworkLink<MSG>) : ForgeMsgWrapper<MSG>(link) {
        override fun handler(msg: MSG, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().run {
                enqueueWork {
                    msg.onServerReceived(ServerMsg.MsgContext(sender!!))
                }
                packetHandled = true
            }
        }
    }

    class ForgeClientMsgWrapper<MSG : ClientMsg>(override val link: INetworkLink<MSG>) : ForgeMsgWrapper<MSG>(link) {
        override fun handler(msg: MSG, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().run {
                enqueueWork {
                    msg.onClientReceived(ClientMsg.MsgContext(MinecraftClient.getInstance()))
                }
                packetHandled = true
            }
        }
    }

    override fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>, id: Identifier): Boolean {
        val dummy = createDummyChannel(id)
        val wrapper = ForgeServerMsgWrapper(link)
        channelMap[link] = dummy
        dummy.registerMessage(1, link.kClass.java, wrapper::writer, wrapper::reader, wrapper::handler)
        return dummy != null
    }

    override fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>, id: Identifier): Boolean {
        val dummy = createDummyChannel(id)
        val wrapper = ForgeClientMsgWrapper(link)
        channelMap[link] = dummy
        dummy?.registerMessage(1, link.kClass.java, wrapper::writer, wrapper::reader, wrapper::handler)
        return dummy != null
    }

    override fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M, msgId: Identifier) {
        val channel = channelMap[link]
        channel!!.sendToServer(msg)
    }

    override fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity, msgId: Identifier) {
        val channel = channelMap[link]
        channel!!.sendTo(msg, player.networkHandler.connection, NetworkDirection.PLAY_TO_CLIENT)
    }

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        reg.register(Kambrik.idOf(thingId), obj)
        return obj
    }

}