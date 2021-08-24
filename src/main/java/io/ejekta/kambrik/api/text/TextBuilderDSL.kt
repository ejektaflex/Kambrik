package io.ejekta.kambrik.api.text

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
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
    var root: T
) {

    fun format(vararg formats: Formatting) {
        root.formatted(*formats)
    }

    fun color(color: Int) {
        root.style = root.style.withColor(TextColor.fromRgb(color))
    }

    fun onClick(event: ClickEvent) {
        root.style = root.style.withClickEvent(event)
    }

    fun onHover(event: HoverEvent) {
        root.style = root.style.withHoverEvent(event)
    }

    fun onHoverShowItem(itemStack: ItemStack) {
        onHover(
            HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                HoverEvent.ItemStackContent(itemStack)
            )
        )
    }

    fun onHoverShowText(text: Text) {
        onHover(
            HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                text
            )
        )
    }

    fun onHoverShowText(inFunc: KambrikTextBuilder<LiteralText>.() -> Unit) {
        onHover(
            HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                textLiteral("", inFunc)
            )
        )
    }

    fun onHoverShowEntity(entity: Entity) {
        onHover(
            HoverEvent(
                HoverEvent.Action.SHOW_ENTITY,
                HoverEvent.EntityContent(entity.type, entity.uuid, entity.name)
            )
        )
    }

    fun newLine() = textLiteral("\n")

    operator fun T.invoke(inFunc: KambrikTextBuilder<T>.() -> Unit): KambrikTextBuilder<T> {
        return KambrikTextBuilder(this).apply(inFunc)
    }

    operator fun String.invoke(inFunc: KambrikTextBuilder<LiteralText>.() -> Unit): LiteralText {
        return KambrikTextBuilder(LiteralText(this)).apply(inFunc).root
    }

    operator fun String.unaryPlus() {
        root.append(this)
    }

    operator fun BaseText.unaryPlus() {
        root.append(this)
    }

    operator fun Formatting.unaryPlus() {
        format(this)
    }

    operator fun ClickEvent.unaryPlus() {
        onClick(this)
    }

    operator fun HoverEvent.unaryPlus() {
        onHover(this)
    }

}


fun main() {



    val test = textLiteral("Hello World!") {
        onHoverShowText {
            +Formatting.ITALIC
            +textLiteral("How are you?")
        }
    }

    /*
    val text = LiteralText("Hello ")
        .formatted(Formatting.GOLD, Formatting.ITALIC)
        .append(
            LiteralText(player.displayName)
                .formatted(Formatting.AQUA)
        )
        .append(LiteralText(", how are you?"))

     */

    println(test)
    println(test.string)

}
