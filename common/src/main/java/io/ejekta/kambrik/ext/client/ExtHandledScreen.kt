package io.ejekta.kambrik.ext.client

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier

fun AbstractContainerScreen<*>.drawSimpleCenteredImage(
    context: GuiGraphicsExtractor,
    location: Identifier,
    bgWidth: Int,
    bgHeight: Int,
    texWidth: Int = 256,
    texHeight: Int = 256
) {
    val x = (width - bgWidth) / 2
    val y = (height - bgHeight) / 2
    context.blit(RenderPipelines.GUI_TEXTURED, location, x, y, 0f, 0f, bgWidth, bgHeight, texWidth, texHeight)
}
