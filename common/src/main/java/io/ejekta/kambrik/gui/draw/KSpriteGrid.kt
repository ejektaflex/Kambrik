package io.ejekta.kambrik.gui.draw

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation

open class KSpriteGrid(val location: ResourceLocation, val texWidth: Int, val texHeight: Int) {

    inner class Sprite(
        val u: Float = 0f,
        val v: Float = 0f,
        val width: Int,
        val height: Int
    ) {

        val grid: KSpriteGrid
            get() = this@KSpriteGrid

        fun draw(screen: Screen, context: GuiGraphics, x: Int, y: Int, w: Int = width, h: Int = height) {
            context.blit(location, x, y, u, v, w, h, texWidth, texHeight)
        }
    }

}