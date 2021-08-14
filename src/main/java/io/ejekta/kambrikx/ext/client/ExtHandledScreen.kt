package io.ejekta.kambrikx.ext.client

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

fun HandledScreen<*>.drawSimpleCenteredImage(matrices: MatrixStack, location: Identifier, bgWidth: Int, bgHeight: Int) {
    RenderSystem.setShader { GameRenderer.getPositionTexShader() }
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, location)
    val x = (width - bgWidth) / 2
    val y = (height - bgHeight) / 2
    drawTexture(matrices, x, y, 0, 0, bgWidth, bgHeight)
}
