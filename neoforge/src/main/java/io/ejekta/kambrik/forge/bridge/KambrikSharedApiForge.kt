package io.ejekta.kambrik.forge.bridge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.BridgeSide
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.message.ClientMsg
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.ServerMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.PacketByteBuf.PacketReader
import net.minecraft.network.packet.CustomPayload
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler
import kotlin.jvm.optionals.getOrNull


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

    private val clientMsgMap = mutableMapOf<Identifier, ForgeMsgWrapper<ClientMsg>>()
    private val serverMsgMap = mutableMapOf<Identifier, ForgeMsgWrapper<ServerMsg>>()

    class KambrikMessagePayload<M : Any>(val link: INetworkLink<M>, val msg: M) : CustomPayload {
        override fun write(buf: PacketByteBuf) {
            buf.writeString(link.serializePacket(msg))
        }
        override fun id() = link.id
    }

    open class ForgeMsgWrapper<MSG : Any>(open val link: INetworkLink<MSG>) {

        val packetReader: PacketReader<KambrikMessagePayload<MSG>>
            get() {
                return PacketReader {
                    KambrikMessagePayload(link, link.deserializePacket(it.readString()))
                }
            }

        val payloadHandler: IPlayPayloadHandler<KambrikMessagePayload<MSG>>
            get() {
                return IPlayPayloadHandler { arg, playPayloadContext ->
                    when (arg.msg) {
                        is ServerMsg -> {
                            println("Server msg!")
                            playPayloadContext.workHandler.submitAsync {
                                val player = playPayloadContext.player.getOrNull() as? ServerPlayerEntity
                                println("Player?: $player")
                                arg.msg.onServerReceived(ServerMsg.MsgContext(player!!))
                            }
                        }
                        is ClientMsg -> {
                            println("Client msg!")
                            playPayloadContext.workHandler.submitAsync {
                                val player = playPayloadContext.player.getOrNull()
                                println("Player?: $player")
                                arg.msg.onClientReceived(ClientMsg.MsgContext(MinecraftClient.getInstance()))
                            }
                        }
                    }
                }
            }

    }



    // normally subscribeevent
    private fun testRegisterServerPackets(event: RegisterPayloadHandlerEvent) {
        val registrar = event.registrar(Kambrik.ID)

        for ((msgId, serverMsg) in serverMsgMap) {
            registrar.play(msgId, serverMsg.packetReader, serverMsg.payloadHandler)
        }

        for ((msgId, clientMsg) in clientMsgMap) {
            registrar.play(msgId, clientMsg.packetReader, clientMsg.payloadHandler)
        }


    }



    override fun <M : ServerMsg> registerServerMessage(link: INetworkLink<M>): Boolean {
        serverMsgMap[link.id] = ForgeMsgWrapper(link as INetworkLink<ServerMsg>)
        return true
    }

    override fun <M : ClientMsg> registerClientMessage(link: INetworkLink<M>): Boolean {
        clientMsgMap[link.id] = ForgeMsgWrapper(link as INetworkLink<ClientMsg>)
        return true
    }

    override fun <M : ServerMsg> sendMsgToServer(link: INetworkLink<M>, msg: M) {
        PacketDistributor.SERVER.noArg().send(KambrikMessagePayload(link, msg))
    }

    override fun <M : ClientMsg> sendMsgToClient(link: INetworkLink<M>, msg: M, player: ServerPlayerEntity) {
        PacketDistributor.PLAYER.with(player).send(KambrikMessagePayload(link, msg))
    }

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        reg.register(Kambrik.idOf(thingId), obj)
        return obj
    }

}