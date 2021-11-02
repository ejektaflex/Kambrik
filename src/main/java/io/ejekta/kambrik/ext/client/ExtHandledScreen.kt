package io.ejekta.kambrik.ext.client

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

fun HandledScreen<*>.drawSimpleCenteredImage(
    matrices: MatrixStack,
    location: Identifier,
    bgWidth: Int,
    bgHeight: Int,
    texWidth: Int = 256,
    texHeight: Int = 256
) {
    RenderSystem.setShader { GameRenderer.getPositionTexShader() }
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, location)
    val x = (width - bgWidth) / 2
    val y = (height - bgHeight) / 2
    DrawableHelper.drawTexture(matrices, x, y, 0f, 0f, bgWidth, bgHeight,  texWidth, texHeight)
}
