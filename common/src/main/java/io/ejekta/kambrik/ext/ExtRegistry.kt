package io.ejekta.kambrik.ext

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

fun Identifier(a: String, b: String) = ResourceLocation.fromNamespaceAndPath(a, b)

val Item.id: ResourceLocation?
    get() = BuiltInRegistries.ITEM.getKey(this)

val ItemStack.id: ResourceLocation?
    get() = BuiltInRegistries.ITEM.getKey(item)

val EntityType<*>.id: ResourceLocation?
    get() = BuiltInRegistries.ENTITY_TYPE.getKey(this)

// Helper for getting a nullable ID
fun <T : Any> Registry<T>.getKeyNullable(thing: T): ResourceLocation? {
    return wrapAsHolder(thing).unwrapKey().getOrNull()?.location()
}

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
