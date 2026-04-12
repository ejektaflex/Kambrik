package io.ejekta.kambrik.gui.draw

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier

open class KSpriteGrid(val location: Identifier, val texWidth: Int, val texHeight: Int) {

    inner class Sprite(
        val u: Float = 0f,
        val v: Float = 0f,
        val width: Int,
        val height: Int
    ) {

        val grid: KSpriteGrid
            get() = this@KSpriteGrid

        fun draw(screen: Screen, context: GuiGraphicsExtractor, x: Int, y: Int, w: Int = width, h: Int = height) {
            context.blit(RenderPipelines.GUI_TEXTURED, location, x, y, u, v, w, h, texWidth, texHeight)
        }
    }

}
