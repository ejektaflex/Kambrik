package io.ejekta.kambrik.internal

import io.ejekta.kambrik.ext.edit
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.kambrik.registration.KambrikAutoRegistrar.Companion.serialComponent
import io.ejekta.kambrik.text.textLiteral
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import java.util.function.Consumer

object TestContent : KambrikAutoRegistrar {

    @Serializable @JvmRecord
    data class ItemData(val timesUsed: Int = 0, val place: @Contextual ItemStack = ItemStack.EMPTY)

    val MY_DATA by serialComponent<ItemData>("test_data")

    val TEST_ITEM by "test_item" forItem { props ->
        object : Item(props.stacksTo(16).rarity(Rarity.UNCOMMON).component(MY_DATA, ItemData(0, ItemStack.EMPTY))) {

            override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResult {
                if (pPlayer is ServerPlayer) {
                    println("Item used!")
                    val stack = pPlayer.getItemInHand(pUsedHand)
                    stack.edit(MY_DATA) { curr -> ItemData((curr?.timesUsed ?: 0) + 1, ItemStack(Items.ACACIA_BUTTON)) }
                    println()
                }
                return super.use(pLevel, pPlayer, pUsedHand)
            }

            override fun appendHoverText(pStack: ItemStack, pContext: TooltipContext, pDisplay: TooltipDisplay, pBuilder: Consumer<Component>, pTooltipFlag: TooltipFlag) {
                val myData = pStack.get(MY_DATA)
                pBuilder.accept(textLiteral("Times Used: ${myData?.timesUsed}"))
                pBuilder.accept(textLiteral("Holding Item: ") {
                    myData?.place?.let { add(it.displayName) }
                })
                super.appendHoverText(pStack, pContext, pDisplay, pBuilder, pTooltipFlag)
            }

        }
    }

    override fun getId() = "kambrik"

}
