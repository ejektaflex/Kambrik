package io.ejekta.kambrik.ext.client

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation

fun AbstractContainerScreen<*>.drawSimpleCenteredImage(
    context: GuiGraphics,
    location: ResourceLocation,
    bgWidth: Int,
    bgHeight: Int,
    texWidth: Int = 256,
    texHeight: Int = 256
) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader)
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, location)
    val x = (width - bgWidth) / 2
    val y = (height - bgHeight) / 2
    context.blit(location, x, y, 0f, 0f, bgWidth, bgHeight,  texWidth, texHeight)
}
