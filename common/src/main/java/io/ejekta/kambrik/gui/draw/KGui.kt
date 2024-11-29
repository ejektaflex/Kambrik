package io.ejekta.kambrik.gui.draw

import io.ejekta.kambrik.gui.screen.KambrikScreenCommon
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity


class KGui(
    val screen: Screen,
    private val coordFunc: () -> Pair<Int, Int>,
    var x: Int = 0,
    var y: Int = 0,
    private val func: KGuiDsl.() -> Unit = {}
) {

    val logic: KambrikScreenCommon
        get() = screen as KambrikScreenCommon

    val entityRenderCache = mutableMapOf<EntityType<*>, LivingEntity>()

    fun draw(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float? = null) {
        logic.boundsStack.clear()
        logic.areaClickStack.clear()
        val toDraw = logic.modalStack.lastOrNull() ?: func // Draw top of modal stack, or func if not exists
        val dsl = KGuiDsl(this, context, mouseX, mouseY, delta).draw(toDraw)
    }

    fun pushModal(dsl: KGuiDsl.() -> Unit) = logic.modalStack.add(dsl)

    fun absX(relX: Int = 0) = x + coordFunc().first + relX

    fun absY(relY: Int = 0) = y + coordFunc().second + relY

}