package io.ejekta.kambrik.ext.fapi

import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.render.item.ItemRenderer

val Screen.buttons: List<ClickableWidget>
    get() = Screens.getButtons(this)

val Screen.client: MinecraftClient
    get() = Screens.getClient(this)

val Screen.itemRenderer: ItemRenderer
    get() = MinecraftClient.getInstance().itemRenderer

val Screen.textRenderer: TextRenderer
    get() = Screens.getTextRenderer(this)

