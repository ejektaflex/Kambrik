package io.ejekta.kambrik.fabric.bridge

import io.ejekta.kambrik.bridge.BridgePlatform
import io.ejekta.kambrik.bridge.KambrikSharedApi
import io.ejekta.kambrik.registration.KambrikRegistrar
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.kambrikx.serial.toSimplePacketCodec
import kotlinx.serialization.KSerializer
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.nio.file.Path

class KambrikSharedApiFabric : KambrikSharedApi {

    override val platform: BridgePlatform
        get() = BridgePlatform.FABRIC

    // Messaging

    override fun isOnClient(): Boolean {
        return FabricLoader.getInstance().environmentType == EnvType.CLIENT
    }

    override fun isOnServer(): Boolean {
        return FabricLoader.getInstance().environmentType == EnvType.SERVER
    }

    override fun <M : KambrikMsg> registerClientMessage(serializer: KSerializer<M>, id: CustomPacketPayload.Type<M>): Boolean {
        PayloadTypeRegistry.clientboundPlay().register(id, serializer.toSimplePacketCodec())
        if (!isOnClient()) {
            return true
        }

        @Suppress("UNCHECKED_CAST")
        return clientMessageRegistrar?.invoke(id as CustomPacketPayload.Type<out KambrikMsg>) ?: true
    }

    override fun <M : KambrikMsg> registerServerMessage(serializer: KSerializer<M>, id: CustomPacketPayload.Type<M>): Boolean {
        PayloadTypeRegistry.serverboundPlay().register(id, serializer.toSimplePacketCodec())
        return ServerPlayNetworking.registerGlobalReceiver(id) { payload, context ->
            (payload as KambrikMsg).onServerReceived(KambrikMsg.MsgContext(context.player()))
        }
    }

    override fun <M : KambrikMsg> sendMsgToClient(msg: M, player: ServerPlayer) {
        ServerPlayNetworking.send(player, msg)
    }

    override fun <M : KambrikMsg> sendMsgToServer(msg: M) {
        clientMessageSender?.invoke(msg)
    }

    // Registration

    override fun <T : Any> register(autoReg: KambrikAutoRegistrar, reg: Registry<T>, thingId: String, obj: T): T {
        return KambrikRegistrar.register(autoReg, reg, thingId, lazyOf(obj)).value
    }

    override fun <T : BlockEntity> createBlockEntityType(
        factory: (pos: BlockPos, state: BlockState) -> T,
        validBlocks: Set<Block>
    ): BlockEntityType<T> {
        return BlockEntityType(
            BlockEntityType.BlockEntitySupplier { pos, state -> factory(pos, state) },
            validBlocks
        )
    }

    override fun getConfigDir(): Path {
        return FabricLoader.getInstance().configDir
    }

    companion object {
        var clientMessageRegistrar: ((CustomPacketPayload.Type<out KambrikMsg>) -> Boolean)? = null
        var clientMessageSender: ((KambrikMsg) -> Unit)? = null
    }

}
