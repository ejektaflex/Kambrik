package io.ejekta.kambrik.gui.draw

import com.mojang.blaze3d.vertex.PoseStack
import io.ejekta.kambrik.gui.draw.reactor.MouseReactor
import io.ejekta.kambrik.text.KambrikTextBuilder
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import kotlin.math.max
import kotlin.math.min

data class KGuiDsl(val ctx: KGui, val context: GuiGraphics, val mouseX: Int, val mouseY: Int, val delta: Float?) {

    val fontRenderer: Font
        get() = Minecraft.getInstance().font

    val itemRenderer: ItemRenderer
        get() = Minecraft.getInstance().itemRenderer

    private val frameDeferredTasks = mutableListOf<KGuiDsl.() -> Unit>()

    operator fun invoke(func: KGuiDsl.() -> Unit) = apply(func)

    fun draw(func: KGuiDsl.() -> Unit): KGuiDsl {
        apply(func)
        doLateDeferral()
        return this
    }

    private fun defer(func: KGuiDsl.() -> Unit) {
        frameDeferredTasks.add(func)
    }

    private fun doLateDeferral() {
        frameDeferredTasks.forEach { func -> apply(func) }
        frameDeferredTasks.clear()
    }

    fun offset(x: Int, y: Int, func: KGuiDsl.() -> Unit) {
        ctx.x += x
        ctx.y += y
        apply(func)
        ctx.x -= x
        ctx.y -= y
    }

    fun rect(x: Int, y: Int, w: Int, h: Int, color: Int, alpha: Int = 0xFF, func: KGuiDsl.() -> Unit = {}) {
        offset(x, y) {
            val sx = ctx.absX()
            val sy = ctx.absY()
            context.fill(sx, sy, sx + w, sy + h, (alpha shl 24) + color)
            apply(func)
        }
    }

    fun rect(w: Int, h: Int, color: Int, alpha: Int = 0xFF, func: KGuiDsl.() -> Unit = {}) {
        rect(0, 0, w, h, color, alpha, func)
    }

    fun itemStackIcon(stack: ItemStack, x: Int = 0, y: Int = 0) {
        context.renderItem(stack, ctx.absX(x), ctx.absY(y))
    }

    fun itemStackOverlay(stack: ItemStack, x: Int = 0, y: Int = 0) {
        context.renderItem(stack, x, y)
    }

    fun itemStack(stack: ItemStack, x: Int = 0, y: Int = 0) {
        itemStackIcon(stack, x, y)
        itemStackOverlay(stack, x, y)
    }

    fun itemStackWithTooltip(stack: ItemStack, x: Int, y: Int) {
        itemStack(stack, x, y)
        onHover(x, y, 18, 18) {
            context.renderTooltip(fontRenderer, stack, x, y)
        }
    }

    fun onHover(x: Int, y: Int, w: Int, h: Int, func: KGuiDsl.() -> Unit) {
        if (KRect.isInside(mouseX, mouseY, ctx.absX(x), ctx.absY(y), w, h)) {
            apply(func)
        }
    }

    fun onHover(w: Int, h: Int, func: KGuiDsl.() -> Unit) {
        onHover(0, 0, w, h, func)
    }

    fun tooltip(components: List<Component>) {
        tooltipText(components.map { it.visualOrderText })
    }

    fun tooltipText(texts: List<FormattedCharSequence>) {
        defer {
            context.renderTooltip(
                fontRenderer,
                texts,
                mouseX,
                mouseY
            )
        }
    }

    fun tooltip(func: KambrikTextBuilder<MutableComponent>.() -> Unit) {
        tooltipText(listOf(textLiteral("", func).visualOrderText))
    }

    fun text(x: Int, y: Int, text: Component) {
        context.drawString(fontRenderer, text, ctx.absX(x), ctx.absY(y), 0xFFFFFF, false)
    }

    fun text(x: Int = 0, y: Int = 0, textDsl: KambrikTextBuilder<MutableComponent>.() -> Unit) {
        text(x, y, textLiteral("", textDsl))
    }

    fun textNoShadow(x: Int, y: Int, text: Component) {
        context.drawString(fontRenderer, text, ctx.absX(x), ctx.absY(y), 0xFFFFFF, false)
    }

    fun textNoShadow(x: Int = 0, y: Int = 0, textDsl: KambrikTextBuilder<MutableComponent>.() -> Unit) {
        textNoShadow(x, y, textLiteral("", textDsl))
    }

    fun textCentered(x: Int, y: Int, text: Component) {
        context.drawString(
            fontRenderer,
            text,
            ctx.absX(x) - fontRenderer.width(text) / 2,
            ctx.absY(y),
            0xFFFFFF,
            false
        )
    }

    fun textCentered(x: Int = 0, y: Int = 0, textDsl: KambrikTextBuilder<MutableComponent>.() -> Unit) {
        textCentered(x, y, textLiteral("", textDsl))
    }

    fun textImmediate(x: Int, y: Int, text: Component) {
        val poseStack = PoseStack()
        poseStack.translate(0.0, 0.0, 201.0)
//        textRenderer.draw
        // TODO allocator visibility? How is this done now, anyways?
        //val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().allocator)

//        textRenderer.draw(
//            text,
//            (ctx.absX(x) + textRenderer.getWidth(text)).toFloat(),
//            ctx.absY(y).toFloat(),
//            0xFFFFFF,
//            true,
//            PoseStack.peek().positionMatrix,
//            immediate,
//            TextRenderer.TextLayerType.NORMAL,
//            0,
//            LightmapTextureManager.MAX_LIGHT_COORDINATE
//        )
//        immediate.draw()
    }

    fun sprite(sprite: KSpriteGrid.Sprite, x: Int = 0, y: Int = 0, w: Int = sprite.width, h: Int = sprite.height, func: (AreaDsl.() -> Unit)? = null) {
        sprite.draw(
            ctx.screen,
            context,
            ctx.absX(x),
            ctx.absY(y),
            w,
            h
        )
        func?.let {
            offset(x, y) {
                area(w, h, it)
            }
        }
    }

    fun img(id: ResourceLocation, w: Int, h: Int, x: Int = 0, y: Int = 0, func: (AreaDsl.() -> Unit)? = null) {
        context.blitSprite(id, ctx.absX(x), ctx.absY(y), w, h)
        func?.let { area(x, y, w, h, it) }
    }

    fun spriteCenteredInScreen(sprite: KSpriteGrid.Sprite, func: AreaDsl.() -> Unit) {
        offset(ctx.screen.width / 2 - sprite.width / 2, ctx.screen.height / 2 - sprite.height / 2) {
            sprite(sprite)
        }
    }

    fun widget(kWidget: KWidget, relX: Int = 0, relY: Int = 0) {
        offset(relX, relY) {
            kWidget.doDraw(this)
        }
    }

    fun isHovered(w: Int, h: Int): Boolean {
        return isHovered(0, 0, w, h)
    }

    fun isHovered(startX: Int, startY: Int, w: Int, h: Int): Boolean {
        return mouseX >= ctx.absX(startX) && mouseX <= ctx.absX(startX + w)
                && mouseY >= ctx.absY(startY) && mouseY <= ctx.absY(startY + h)
    }

    fun area(w: Int, h: Int, func: AreaDsl.() -> Unit) {
        areaDsl.adjusted(w, h, func)
    }

    fun area(relX: Int, relY: Int, w: Int, h: Int, func: AreaDsl.() -> Unit) {
        offset(relX, relY) {
            area(w, h, func)
        }
    }

    private val areaDsl = AreaDsl(0, 0)

    inner class AreaDsl internal constructor(var w: Int, var h: Int) {

        val dsl: KGuiDsl
            get() = this@KGuiDsl

        operator fun invoke(dsl: AreaDsl.() -> Unit) = apply(dsl)

        val isHovered: Boolean
            get() = isHovered(w, h)

        internal fun adjusted(newW: Int, newH: Int, func: AreaDsl.() -> Unit) {
            val oldW = w
            val oldH = h
            w = newW
            h = newH
            apply(func)
            w = oldW
            h = oldH
        }

        fun livingEntity(entity: LivingEntity, size: Double = min(w, h).toDouble()) {
            val dims = entity.getDimensions(entity.pose)
            val maxDim = (1 / max(dims.height, dims.width) * size).toInt().coerceAtLeast(1)
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                context,
                ctx.absX(), ctx.absY(),
                ctx.absX(w), ctx.absY(h),
                maxDim, 0.25f,
                mouseX.toFloat(), mouseY.toFloat(),
                entity
            )
        }

        fun livingEntity(entityType: EntityType<out LivingEntity>, size: Double = 20.0) {
            val eet = Minecraft.getInstance().level?.let { entityType.create(it) } ?: return
            val entity = ctx.entityRenderCache.getOrPut(entityType) { eet }
            livingEntity(entity, size)
        }

        fun reactWith(mouseReactor: MouseReactor) {
            val boundsRect = KRect(ctx.absX(), ctx.absY(), w, h)
            // Run hover event if hovering
            if (boundsRect.isInside(mouseX, mouseY)) {
                mouseReactor.onHover(mouseX - boundsRect.x, mouseY - boundsRect.y)
            }
            // Add to stack for later event handling
            ctx.logic.boundsStack.add(0, mouseReactor to boundsRect)
        }

        fun rect(color: Int, alpha: Int = 0xFF, func: KGuiDsl.() -> Unit = {}) {
            rect(w, h, color, alpha, func)
        }

        fun onHover(func: KGuiDsl.() -> Unit) {
            onHover(w, h, func)
        }

        fun textCentered(y: Int, text: Component) {
            textCentered(w / 2, y, text)
        }

        fun widgetCentered(widget: KWidget, func: KGuiDsl.() -> Unit = {}) {
            offset(w / 2 - widget.width / 2, h / 2 - widget.height / 2) {
                widget(widget)
                apply(func)
            }
        }

    }

}