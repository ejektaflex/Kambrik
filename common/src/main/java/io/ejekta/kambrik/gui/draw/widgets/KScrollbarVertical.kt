package io.ejekta.kambrik.gui.draw.widgets

import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KSpriteGrid

class KScrollbarVertical(
    scrollHeight: Int,
    knobSprite: KSpriteGrid.Sprite,
    backgroundColor: Int? = null
) : KScrollbar(knobSprite, backgroundColor) {
    override val height = scrollHeight
    override val width = knobSprite.width

    override val scrollbarSize: Int
        get() = height

    override val knobSize: Int
        get() = knobSprite.height

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        super.onDraw(area)
        area.dsl {
            val relY = mouseY - ctx.absY()
            val newPos = knobPos(relY)

            if (reactor.isDragging) {
                sprite(knobSprite, y = newPos)
                dragStart = newPos
            } else {
                sprite(knobSprite, y = dragStart)
            }
        }
    }
}