package io.ejekta.kambrikx.ext.util

fun <T : Any> List<T>.weightedRandomBy(func: T.() -> Int): T {
    val mapped = map { it to func(it) }.toMap()
    return mapped.weightedRandom()
}

fun <T : Any> Map<T, Int>.weightedRandom(): T {
    val sum = values.sum()

    if (sum == 0) {
        return keys.random()
    }

    var point = (1..sum).random()

    for ((item, weight) in this) {
        if (point <= weight) {
            return item
        }
        point -= weight
    }
    return keys.last()
}