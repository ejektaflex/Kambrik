package io.ejekta.kambrik.gui.screen

import io.ejekta.kambrik.gui.draw.KGui
import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KRect
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

abstract class KambrikScreen(title: Component) : Screen(title), KambrikScreenCommon {
    override val boundsStack = mutableListOf<Pair<MouseReactor, KRect>>()
    override val areaClickStack = mutableListOf<Pair<() -> Unit, KRect>>()
    override val modalStack = mutableListOf<KGuiDsl.() -> Unit>()

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        super<KambrikScreenCommon>.mouseClicked(event, doubleClick)
        return super<Screen>.mouseClicked(event, doubleClick)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        super<KambrikScreenCommon>.mouseReleased(event)
        return super<Screen>.mouseReleased(event)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super<KambrikScreenCommon>.mouseMoved(mouseX, mouseY)
        super<Screen>.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, hAmount: Double, vAmount: Double): Boolean {
        super<KambrikScreenCommon>.mouseScrolled(mouseX, mouseY, hAmount, vAmount)
        return super<Screen>.mouseScrolled(mouseX, mouseY, hAmount, vAmount)
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        onDrawBackground(context, mouseX, mouseY, delta)
        super.extractRenderState(context, mouseX, mouseY, delta)
        onDrawForeground(context, mouseX, mouseY, delta)
    }

    fun kambrikGui(func: KGuiDsl.() -> Unit) = KGui(
        this, { 0 to 0 }
    ) {
        apply(func)
    }

}
