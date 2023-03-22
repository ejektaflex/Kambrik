package io.ejekta.kambrik.ext.commands

import com.mojang.brigadier.suggestion.SuggestionsBuilder

fun SuggestionsBuilder.addAll(list: List<String>) {
    for (item in list) {
        suggest(item)
    }
}