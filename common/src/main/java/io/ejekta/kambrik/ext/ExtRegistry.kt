package io.ejekta.kambrik.ext

import net.minecraft.registry.Registry
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier


fun <T> Registry<T>.register(id: Identifier, obj: T) {
    Registry.register(this, id, obj)
}

fun <T> Registry<T>.registerForMod(modId: String, items: () -> Map<String, T>) {
    for ((itemId, item) in items()) {
        register(Identifier(modId, itemId), item)
    }
}

operator fun <T> Registry<T>.get(tagKey: TagKey<T>): List<T> {
    return iterateEntries(tagKey).toList() as List<T>
}
