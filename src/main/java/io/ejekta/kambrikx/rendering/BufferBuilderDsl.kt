package io.ejekta.kambrikx.rendering

import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import java.awt.Color

fun tessellate(glMode: Int, vertexFormat: VertexFormat, worldRendererScope: BufferBuilderScope.() -> Unit) {
    val tes = Tessellator.getInstance()
    BufferBuilderScope(tes.buffer).apply(worldRendererScope)
    tes.buffer.end()
    tes.draw()
}

class BufferBuilderScope(private val renderer: BufferBuilder) {
    private var requiresNext = false

    fun vertex(vertexScope: VertexScope.() -> Unit) {
        if (requiresNext) renderer.next()

        with(VertexScope().apply(vertexScope)) {
            if (pos != null) renderer.vertex(pos!!.first, pos!!.second, pos!!.third)
            if (tex != null) renderer.texture(tex!!.first, tex!!.second)
            if (color != null) renderer.color(color!!.red, color!!.green, color!!.blue, color!!.alpha)
        }
        requiresNext = true
    }
}

class VertexScope {
    var pos: Triple<Double, Double, Double>? = null
        private set
    var tex: Pair<Float, Float>? = null
        private set
    var color: Color? = null
        private set

    fun pos(x: Number, y: Number, z: Number) {
        pos = Triple(x.toDouble(), y.toDouble(), z.toDouble())
    }
    fun tex(u: Number, v: Number) {
        tex = u.toFloat() to v.toFloat()
    }

    fun color(red: Float, green: Float, blue: Float, alpha: Float = 1f) {
        color = Color(red * 255, green * 255, blue * 255, alpha * 255)
    }
    fun color(red: Int, green: Int, blue: Int, alpha: Int = 255) {
        color = Color(red, green, blue, alpha)
    }
    fun color(rgba: Int) {
        color = Color(rgba)
    }
    fun color(color: Color) {
        this.color = color
    }
}