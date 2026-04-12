package io.ejekta.kambrik.fabric.registration

import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType

fun <T : AbstractContainerMenu> KambrikAutoRegistrar.forScreen(key: String, factory: MenuType.MenuSupplier<T>, requiredFeatures: FeatureFlagSet): Lazy<MenuType<T>> {
    @Suppress("UNCHECKED_CAST")
    return key.forRegistration(BuiltInRegistries.MENU) { MenuType(factory, requiredFeatures) } as Lazy<MenuType<T>>
}

fun <T : AbstractContainerMenu, D : Any> KambrikAutoRegistrar.forExtendedScreen(
    key: String,
    factory: ExtendedMenuType.ExtendedFactory<T, D>,
    packetCodec: StreamCodec<RegistryFriendlyByteBuf, D>
): Lazy<MenuType<T>> {
    @Suppress("UNCHECKED_CAST")
    return key.forRegistration(BuiltInRegistries.MENU) { ExtendedMenuType<T, D>(factory, packetCodec) } as Lazy<MenuType<T>>
}
