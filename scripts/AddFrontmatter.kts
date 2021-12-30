package io.ejekta.testing

import java.io.File

fun main() {
    println("Running 'Add Frontmatter' Script")

    val dokkaFolder = File("../build/dokka/gfm")

    for (item in dokkaFolder.walkTopDown()) {
        if (item.isFile && item.extension == "md") {
            println("Found file! $item")
            val text = item.readLines()
            val frontMatter = """
                ---
                pageClass: dokka
                editLink: false
                ---
            """.trimIndent()
            item.writeText(frontMatter + "\n" + text.joinToString("\n"))
        }
    }
}

main()