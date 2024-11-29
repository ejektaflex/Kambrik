package io.ejekta.kambrik.gui.draw

import io.ejekta.kambrik.gui.draw.KGuiDsl
import net.minecraft.client.gui.components.events.ContainerEventHandler
import net.minecraft.client.gui.components.events.GuiEventListener

open class KWidgetDsl(
    var drawFunc: KGuiDsl.() -> Unit = {},
    open val width: Int,
    open val height: Int,
) : ContainerEventHandler {

    fun onDraw(func: KGuiDsl.() -> Unit) {
        drawFunc = func
    }

    val children = mutableListOf<GuiEventListener>()

    override fun children(): MutableList<out GuiEventListener> {
        return children
    }

    override fun isDragging(): Boolean {
        return false
    }

    override fun setDragging(dragging: Boolean) {
        //
    }

    override fun getFocused(): GuiEventListener? {
        return null
    }

    override fun setFocused(focused: GuiEventListener?) {
        //
    }

}