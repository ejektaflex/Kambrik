package io.ejekta.kambrik.text

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.text.*
import net.minecraft.util.Formatting
import java.util.*


fun textLiteral(str: String = "", func: KambrikTextBuilder<MutableText>.() -> Unit = {}): MutableText {
    return textBuilder(MutableText.of(LiteralTextContent(str)), func)
}

fun textTranslate(key: String, fallback: String, args: Array<Any> = emptyArray(), func: KambrikTextBuilder<MutableText>.() -> Unit = {}): MutableText {
    return textBuilder(MutableText.of(TranslatableTextContent(key, fallback, args)), func)
}

fun textKeybind(key: String, func: KambrikTextBuilder<MutableText>.() -> Unit = {}): MutableText {
    return textBuilder(MutableText.of(KeybindTextContent(key)), func)
}

fun textScore(name: String, objective: String, func: KambrikTextBuilder<MutableText>.() -> Unit = {}): MutableText {
    return textBuilder(MutableText.of(ScoreTextContent(name, objective)), func)
}

fun textSelector(pattern: String, separator: Text?, func: KambrikTextBuilder<MutableText>.() -> Unit = {}): MutableText {
    return textBuilder(MutableText.of(SelectorTextContent(pattern, Optional.ofNullable(separator))), func)
}

internal fun <T : MutableText> textBuilder(starterText: T, func: KambrikTextBuilder<T>.() -> Unit): T {
    val builder = KambrikTextBuilder(starterText)
    builder.func()
    return builder.root
}

class KambrikTextBuilder<T : MutableText>(
    var root: T
) {

    fun format(vararg formats: Formatting) {
        root.formatted(*formats)
    }

    fun color(color: Int) {
        root.style = root.style.withColor(TextColor.fromRgb(color))
    }

    var bold: Boolean
        get() = root.style.isBold
        set(value) {
            root.style = root.style.withBold(value)
        }

    var italics: Boolean
        get() = root.style.isItalic
        set(value) {
            root.style = root.style.withItalic(value)
        }

    var strikeThrough: Boolean
        get() = root.style.isStrikethrough
        set(value) {
            root.style = root.style.withStrikethrough(value)
        }

    var obfuscated: Boolean
        get() = root.style.isObfuscated
        set(value) {
            root.style = root.style.withObfuscated(value)
        }

    var color: Int
        get() = root.style.color?.rgb ?: 0x000000
        set(value) {
            color(value)
        }

    var clickEvent: ClickEvent?
        get() = root.style.clickEvent
        set(value) {
            root.style = root.style.withClickEvent(value)
        }

    var hoverEvent: HoverEvent?
        get() = root.style.hoverEvent
        set(value) {
            root.style = root.style.withHoverEvent(value)
        }

    fun onHoverShowItem(itemStack: ItemStack) {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_ITEM, HoverEvent.ItemStackContent(itemStack))
    }

    fun onHoverShowText(text: Text) {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, text)
    }

    fun onHoverShowText(inFunc: KambrikTextBuilder<MutableText>.() -> Unit) {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, textLiteral("", inFunc))
    }

    fun onHoverShowEntity(entity: Entity) {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_ENTITY, HoverEvent.EntityContent(entity.type, entity.uuid, entity.name))
    }

    fun newLine() = addLiteral("\n")

    operator fun T.invoke(inFunc: KambrikTextBuilder<T>.() -> Unit): KambrikTextBuilder<T> {
        return KambrikTextBuilder(this).apply(inFunc)
    }

    operator fun String.invoke(inFunc: KambrikTextBuilder<MutableText>.() -> Unit): MutableText {
        return KambrikTextBuilder(MutableText.of(LiteralTextContent(this))).apply(inFunc).root
    }

    fun add(text: Text) {
        root.append(text)
    }

    fun addLiteral(str: String, func: KambrikTextBuilder<MutableText>.() -> Unit = {}) {
        root.append(textLiteral(str, func))
    }

    fun addTranslate(key: String, fallback: String, args: Array<Any> = emptyArray(), func: KambrikTextBuilder<MutableText>.() -> Unit = {}) {
        root.append(textTranslate(key, fallback, args, func))
    }

}

