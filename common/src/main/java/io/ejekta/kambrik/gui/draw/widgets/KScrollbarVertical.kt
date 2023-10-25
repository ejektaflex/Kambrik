package io.ejekta.kambrik.gui.draw.widgets

import io.ejekta.kambrik.gui.draw.KGuiDsl
import io.ejekta.kambrik.gui.draw.KSpriteGrid
import net.minecraft.util.Identifier

class KScrollbarVertical(
    scrollHeight: Int,
    scrollWidth: Int,
    override val knobSize: Int,
    val knobImg: Identifier?,
    backgroundColor: Int? = null
) : KScrollbar(backgroundColor) {
    override val height = scrollHeight
    override val width = scrollWidth

    override val scrollbarSize: Int
        get() = height

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        super.onDraw(area)
        knobImg?.let {
            area.dsl {
                val relY = mouseY - ctx.absY()
                val newPos = knobPos(relY)

                if (reactor.isDragging) {
                    img(it, width, knobSize, y = newPos)
                    dragStart = newPos
                } else {
                    img(it, width, knobSize, y = dragStart)
                }
            }
        }
    }
}