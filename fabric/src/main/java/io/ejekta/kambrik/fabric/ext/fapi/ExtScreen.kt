package io.ejekta.kambrik.fabric.ext.fapi

import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.entity.ItemRenderer

val Screen.buttons: List<AbstractWidget>
    get() = Screens.getButtons(this)

val Screen.client: Minecraft
    get() = Screens.getClient(this)

val Screen.itemRenderer: ItemRenderer
    get() = Minecraft.getInstance().itemRenderer

val Screen.textRenderer: Font
    get() = Screens.getTextRenderer(this)

