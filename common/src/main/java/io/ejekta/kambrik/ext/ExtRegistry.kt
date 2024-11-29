package io.ejekta.kambrik.ext

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

fun Identifier(a: String, b: String) = ResourceLocation.fromNamespaceAndPath(a, b)

fun <T> Registry<T>.register(id: ResourceLocation, obj: T) {
    Registry.register(this, id, obj)
}

fun <T> Registry<T>.registerForMod(modId: String, items: () -> Map<String, T>) {
    for ((itemId, item) in items()) {
        register(Identifier(modId, itemId), item)
    }
}

operator fun <T> Registry<T>.get(tagKey: TagKey<T>): List<T> {
    return get(tagKey).toList()
}
