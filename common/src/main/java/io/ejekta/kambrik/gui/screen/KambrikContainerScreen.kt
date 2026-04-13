package io.ejekta.kambrik.gui.screen

import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KRect
import io.ejekta.kambrik.gui.draw.KSpriteGrid
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu

abstract class KambrikContainerScreen<AM : AbstractContainerMenu>(
    menu: AM,
    inventory: Inventory,
    title: Component,
    imageWidth: Int = 176,
    imageHeight: Int = 166
) : AbstractContainerScreen<AM>(menu, inventory, title, imageWidth, imageHeight), KambrikScreenCommon {

    override val boundsStack = mutableListOf<Pair<MouseReactor, KRect>>()
    override val areaClickStack = mutableListOf<Pair<() -> Unit, KRect>>()
    override val modalStack = mutableListOf<KGuiDsl.() -> Unit>()

    fun sizeToSprite(sprite: KSpriteGrid.Sprite) {
        width = sprite.width
        height = sprite.height
    }

    override fun extractContents(pGuiGraphics: GuiGraphicsExtractor, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        onDrawBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        super.extractContents(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    override fun onDrawForeground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        /* Pass here */
    }

    override fun extractLabels(pGuiGraphics: GuiGraphicsExtractor, pMouseX: Int, pMouseY: Int) {
        /* Do not draw default labels */
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractRenderState(context, mouseX, mouseY, delta)
        onDrawForeground(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        super<KambrikScreenCommon>.mouseClicked(event, doubleClick)
        return super<AbstractContainerScreen>.mouseClicked(event, doubleClick)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        super<KambrikScreenCommon>.mouseReleased(event)
        return super<AbstractContainerScreen>.mouseReleased(event)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super<KambrikScreenCommon>.mouseMoved(mouseX, mouseY)
        super<AbstractContainerScreen>.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, hAmount: Double, vAmount: Double): Boolean {
        super<KambrikScreenCommon>.mouseScrolled(mouseX, mouseY, hAmount, vAmount)
        return super<AbstractContainerScreen>.mouseScrolled(mouseX, mouseY, hAmount, vAmount)
    }

    fun kambrikGui(func: KGuiDsl.() -> Unit) = KGui(
        this, { leftPos to topPos }
    ) {
        apply(func)
    }


}
