package io.ejekta.kambrikx.items

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

fun kambrikItemGroup(builder: KambrikItemGroupBuilder.() -> Unit) =
    KambrikItemGroupBuilder().apply(builder)

class KambrikItemGroupBuilder {
    lateinit var icon: () -> ItemStack
    val items = mutableListOf<() -> ItemStack>()

    fun icon(icon: () -> ItemStack) { this.icon = icon }
    fun items(vararg items: () -> ItemStack) { this.items.addAll(items) }

    fun build(identifier: Identifier): ItemGroup {
        return FabricItemGroupBuilder.create(identifier)
            .icon(icon)
            .appendItems { _ -> items.map { it() } }
            .build()
    }
}