package io.ejekta.kambrik.gui.screen

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KRect
import io.ejekta.kambrik.gui.draw.KSpriteGrid
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu

abstract class KambrikContainerScreen<AM : AbstractContainerMenu>(
    menu: AM,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<AM>(menu, inventory, title), KambrikScreenCommon {

    override val boundsStack = mutableListOf<Pair<MouseReactor, KRect>>()
    override val areaClickStack = mutableListOf<Pair<() -> Unit, KRect>>()
    override val modalStack = mutableListOf<KGuiDsl.() -> Unit>()

    fun sizeToSprite(sprite: KSpriteGrid.Sprite) {
        width = sprite.width
        height = sprite.height
    }

    // TODO is this needed? :thinkies:
    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        onDrawBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    override fun onDrawForeground(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        /* Pass here */
    }

    override fun renderLabels(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        /* Do not draw default labels */
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)
        onDrawForeground(context, mouseX, mouseY, delta)
        renderTooltip(context, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikScreenCommon>.mouseClicked(mouseX, mouseY, button)
        return super<AbstractContainerScreen>.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikScreenCommon>.mouseReleased(mouseX, mouseY, button)
        return super<AbstractContainerScreen>.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super<KambrikScreenCommon>.mouseMoved(mouseX, mouseY)
        super<AbstractContainerScreen>.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, hAmount: Double, vAmount: Double): Boolean {
        super<KambrikScreenCommon>.mouseScrolled(mouseX, mouseY, hAmount, vAmount)
        return super<AbstractContainerScreen>.mouseScrolled(mouseX, mouseY, hAmount, vAmount)
    }

    fun kambrikGui(clearOnDraw: Boolean = false, func: KGuiDsl.() -> Unit) = KGui(
        this, { leftPos to topPos }
    ) {
        if (clearOnDraw) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        }
        apply(func)
    }


}