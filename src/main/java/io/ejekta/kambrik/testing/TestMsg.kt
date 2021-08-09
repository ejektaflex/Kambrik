package io.ejekta.kambrik.testing

import io.ejekta.kambrik.api.network.KambrikMessages
import io.ejekta.kambrik.api.network.ClientMsg
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

@Serializable
class TestMsg(val num: Int) : ClientMsg() {
    override fun onClientReceived(ctx: MsgContext) {
        println("Got num!: $num")
    }
}

fun test() {
    // to register:
    KambrikMessages.registerClientMessage(
        TestMsg.serializer(),
        Identifier("kambrik", "test_msg")
    )

    // To send the message:
    //TestMsg(100).sendToClient(some_player)

    // We can even auto-serialize some Minecraft data classes.
    class AnotherTestMsg(@Contextual val pos: BlockPos)

    // Heck, we can even pass references to registry objects
    // and refer to them from the other side.
    class FinalTestMsg(@Contextual val bucketLogic: Item) : ClientMsg()

    FinalTestMsg(Items.BUCKET as Item).sendToClients(PlayerLookup.tracking(
        MinecraftClient.getInstance().player
    ))

}

/*
    ### To register the message:


    ### To use the message:


    ### We can also automatically serialize some Minecraft data classes. you do it like so:

 */
