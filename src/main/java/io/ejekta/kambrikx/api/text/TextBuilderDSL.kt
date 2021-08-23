package io.ejekta.kambrikx.api.text

import net.minecraft.text.*


internal fun <T : BaseText> textBuilder(starterText: T, func: KambrikTextBuilder<T>.() -> Unit = {}): T {
    val builder = KambrikTextBuilder(starterText)
    builder.func()
    return builder.root
}

fun textLiteral(str: String = "", func: KambrikTextBuilder<LiteralText>.() -> Unit = {}): LiteralText {
    return textBuilder(LiteralText(str), func)
}

fun textTranslate(key: String, func: KambrikTextBuilder<TranslatableText>.() -> Unit = {}): TranslatableText {
    return textBuilder(TranslatableText(key), func)
}

class KambrikTextBuilder<T : BaseText>(
    var root: T,
    val baseStyle: Style = Style.EMPTY
) {
    operator fun BaseText.unaryPlus() {
        println("Appending ${this.string} to ${root.string}")
        root.append(this)
    }

    //fun

}


fun main() {

    val test = textLiteral("doot") {
        +textLiteral("hai") {
            +textLiteral("derp")
            +textTranslate("my.test.key")
        }
    }

    println(test)
    println(test.string)

}

/*


val a = literalText(



 */