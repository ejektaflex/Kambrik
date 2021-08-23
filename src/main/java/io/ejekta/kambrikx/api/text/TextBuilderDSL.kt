package io.ejekta.kambrikx.api.text

import net.minecraft.text.*
import net.minecraft.util.Formatting
import java.util.*


fun textLiteral(str: String = "", func: KambrikTextBuilder<LiteralText>.() -> Unit = {}): LiteralText {
    return textBuilder(LiteralText(str), func)
}

fun textTranslate(key: String, func: KambrikTextBuilder<TranslatableText>.() -> Unit = {}): TranslatableText {
    return textBuilder(TranslatableText(key), func)
}

fun textKeybind(key: String, func: KambrikTextBuilder<KeybindText>.() -> Unit = {}): KeybindText {
    return textBuilder(KeybindText(key), func)
}

fun textScore(name: String, objective: String, func: KambrikTextBuilder<ScoreText>.() -> Unit = {}): ScoreText {
    return textBuilder(ScoreText(name, objective), func)
}

fun textSelector(pattern: String, separator: Text?, func: KambrikTextBuilder<SelectorText>.() -> Unit = {}): SelectorText {
    return textBuilder(SelectorText(pattern, Optional.ofNullable(separator)), func)
}

internal fun <T : BaseText> textBuilder(starterText: T, func: KambrikTextBuilder<T>.() -> Unit): T {
    val builder = KambrikTextBuilder(starterText)
    builder.func()
    return builder.root
}

class KambrikTextBuilder<T : BaseText>(
    var root: T,
    val baseStyle: Style = Style.EMPTY
) {
    operator fun BaseText.unaryPlus() {
        root.append(this)
    }

    fun format(vararg formats: Formatting) {
        root.formatted(*formats)
    }

    fun color(color: Int) {
        changeStyle { withColor(TextColor.fromRgb(color)) }
    }

    fun onClick(event: ClickEvent) {
        changeStyle { withClickEvent(event) }
    }

    fun onHover(event: HoverEvent) {
        changeStyle { withHoverEvent(event) }
    }

    fun newLine() = textLiteral("\n")

    fun changeStyle(func: Style.() -> Unit) {
        root.style = root.style.apply(func)
    }

}


fun main() {

    val test = textLiteral("doot") {
        format(Formatting.GOLD)
        +newLine()
    }

    println(test)
    println(test.string)

}
