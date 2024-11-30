package io.ejekta.kambrik.neoforge.bridge

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.bridge.BridgePlatform
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.kambrikx.serial.toSimplePacketCodec
import kotlinx.serialization.KSerializer
import net.minecraft.core.Registry
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.ClientPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforge.network.handling.ServerPayloadContext
import net.neoforged.neoforge.network.registration.HandlerThread
import java.nio.file.Path


class KambrikSharedApiForge() : KambrikSharedApi {

    init {
        Kambrik.Logger.debug("Kambrik Shared API (Forge) Initialized.")
    }

    override val platform: BridgePlatform
        get() = BridgePlatform.NEOFORGE

    // Event methods

    override fun isOnClient(): Boolean {
        return FMLEnvironment.dist == Dist.CLIENT
    }

    override fun isOnServer(): Boolean {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER
    }

    // Messaging

    private val clientMsgMap = mutableListOf<ForgeMsgData<KambrikMsg>>()
    private val serverMsgMap = mutableListOf<ForgeMsgData<KambrikMsg>>()

    data class ForgeMsgData<M : KambrikMsg>(val ser: KSerializer<M>, val type: CustomPacketPayload.Type<M>) {
        val streamCodec = ser.toSimplePacketCodec()
        val payloadHandler = IPayloadHandler<M> { p0, p1 ->
            p1.enqueueWork {
                when (p1) {
                    is ClientPayloadContext -> { p0.onClientReceived() }
                    is ServerPayloadContext -> { p0.onServerReceived(KambrikMsg.MsgContext(p1.player())) }
                    else -> throw Exception("No valid payload context for this message serializer: $ser")
                }
            }.exceptionally { throwable ->
                throwable.printStackTrace()
                return@exceptionally null
            }
        }
    }

    // normally subscribeevent
    fun registerPayloads(event: RegisterPayloadHandlersEvent) {
        for (serverMsg in serverMsgMap) {
            Kambrik.Logger.info("Registering ServerMsg: ${serverMsg.type.id}")
            val registrar = event.registrar(serverMsg.type.id.namespace).executesOn(HandlerThread.NETWORK)
            registrar.playToServer(serverMsg.type, serverMsg.streamCodec, serverMsg.payloadHandler)
        }
        for (clientMsg in clientMsgMap) {
            Kambrik.Logger.info("Registering ClientMsg: ${clientMsg.type.id}")
            val registrar = event.registrar(clientMsg.type.id.namespace).executesOn(HandlerThread.NETWORK)
            registrar.playToClient(clientMsg.type, clientMsg.streamCodec, clientMsg.payloadHandler)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <M : KambrikMsg> registerClientMessage(
        serializer: KSerializer<M>,
        id: CustomPacketPayload.Type<M>
    ): Boolean {
        clientMsgMap.add(ForgeMsgData(serializer as KSerializer<KambrikMsg>, id as CustomPacketPayload.Type<KambrikMsg>))
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun <M : KambrikMsg> registerServerMessage(
        serializer: KSerializer<M>,
        id: CustomPacketPayload.Type<M>
    ): Boolean {
        serverMsgMap.add(ForgeMsgData(serializer as KSerializer<KambrikMsg>, id as CustomPacketPayload.Type<KambrikMsg>))
        return true
    }

    override fun <M : KambrikMsg> sendMsgToServer(msg: M) {
        PacketDistributor.sendToServer(msg)
    }

    override fun <M : KambrikMsg> sendMsgToClient(msg: M, player: ServerPlayer) {
        PacketDistributor.sendToPlayer(player, msg)
    }

    // TODO check this
    override fun getConfigDir(): Path {
        return Path.of("config")
    }

    override fun <T> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        reg.register(ResourceLocation.fromNamespaceAndPath(autoReg.getId(), thingId), obj)
        return obj
    }

}