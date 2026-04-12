package io.ejekta.kambrik.fabric.ext.fapi

import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen

val Screen.buttons: List<AbstractWidget>
    get() = Screens.getWidgets(this)

val Screen.client: Minecraft
    get() = Screens.getMinecraft(this)

val Screen.textRenderer: Font
    get() = this.getFont()
