package io.ejekta.kambrik.ext

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

fun Identifier(a: String, b: String) = Identifier.fromNamespaceAndPath(a, b)

val Item.id: Identifier?
    get() = BuiltInRegistries.ITEM.getKey(this)

val ItemStack.id: Identifier?
    get() = BuiltInRegistries.ITEM.getKey(item)

val EntityType<*>.id: Identifier?
    get() = BuiltInRegistries.ENTITY_TYPE.getKey(this)

// Helper for getting a nullable ID
fun <T : Any> Registry<T>.getKeyNullable(thing: T): Identifier? {
    return wrapAsHolder(thing).unwrapKey().getOrNull()?.identifier()
}

fun <T : Any> Registry<T>.register(id: Identifier, obj: T) {
    Registry.register(this, id, obj)
}

fun <T : Any> Registry<T>.registerForMod(modId: String, items: () -> Map<String, T>) {
    for ((itemId, item) in items()) {
        register(Identifier(modId, itemId), item)
    }
}

operator fun <T : Any> Registry<T>.get(tagKey: TagKey<T>): List<T> {
    return getTagOrEmpty(tagKey).map { it.value() }
}
