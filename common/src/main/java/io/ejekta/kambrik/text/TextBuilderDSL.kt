package io.ejekta.kambrik.text

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.*
import net.minecraft.network.chat.contents.KeybindContents
import net.minecraft.network.chat.contents.PlainTextContents
import net.minecraft.network.chat.contents.ScoreContents
import net.minecraft.network.chat.contents.SelectorContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import java.util.*


fun textLiteral(str: String = "", func: KambrikTextBuilder<MutableComponent>.() -> Unit = {}): MutableComponent {
    return textBuilder(MutableComponent.create(PlainTextContents.LiteralContents(str)), func)
}

fun textTranslate(key: String, fallback: String, args: Array<Any> = emptyArray(), func: KambrikTextBuilder<MutableComponent>.() -> Unit = {}): MutableComponent {
    return textBuilder(MutableComponent.create(TranslatableContents(key, fallback, args)), func)
}

fun textKeybind(key: String, func: KambrikTextBuilder<MutableComponent>.() -> Unit = {}): MutableComponent {
    return textBuilder(MutableComponent.create(KeybindContents(key)), func)
}

fun textScore(name: String, objective: String, func: KambrikTextBuilder<MutableComponent>.() -> Unit = {}): MutableComponent {
    return textBuilder(MutableComponent.create(ScoreContents(name, objective)), func)
}

fun textSelector(pattern: String, separator: Component?, func: KambrikTextBuilder<MutableComponent>.() -> Unit = {}): MutableComponent {
    return textBuilder(MutableComponent.create(SelectorContents(pattern, Optional.ofNullable(separator))), func)
}

internal fun <T : MutableComponent> textBuilder(starterText: T, func: KambrikTextBuilder<T>.() -> Unit): T {
    val builder = KambrikTextBuilder(starterText)
    builder.func()
    return builder.root
}

class KambrikTextBuilder<T : MutableComponent>(
    var root: T
) {

    fun format(vararg formats: ChatFormatting) {
        root.withStyle(*formats)
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
        get() = root.style.color?.value ?: 0x000000
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
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_ITEM, HoverEvent.ItemStackInfo(itemStack))
    }

    fun onHoverShowText(text: Component) {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, text)
    }

    fun onHoverShowText(inFunc: KambrikTextBuilder<MutableComponent>.() -> Unit) {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, textLiteral("", inFunc))
    }

    fun onHoverShowEntity(entity: Entity) {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_ENTITY, HoverEvent.EntityTooltipInfo(entity.type, entity.uuid, entity.name))
    }

    fun newLine() = addLiteral("\n")

    operator fun T.invoke(inFunc: KambrikTextBuilder<T>.() -> Unit): KambrikTextBuilder<T> {
        return KambrikTextBuilder(this).apply(inFunc)
    }

    operator fun String.invoke(inFunc: KambrikTextBuilder<MutableComponent>.() -> Unit): MutableComponent {
        return KambrikTextBuilder(MutableComponent.create(PlainTextContents.LiteralContents(this))).apply(inFunc).root
    }

    fun add(text: Component) {
        root.append(text)
    }

    fun addLiteral(str: String, func: KambrikTextBuilder<MutableComponent>.() -> Unit = {}) {
        root.append(textLiteral(str, func))
    }

    fun addTranslate(key: String, fallback: String, args: Array<Any> = emptyArray(), func: KambrikTextBuilder<MutableComponent>.() -> Unit = {}) {
        root.append(textTranslate(key, fallback, args, func))
    }

}

