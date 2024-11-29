package io.ejekta.kambrik.fabric.registration

import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType

fun <T : AbstractContainerMenu> KambrikAutoRegistrar.forScreen(key: String, factory: MenuType.MenuSupplier<T>, requiredFeatures: FeatureFlagSet): Lazy<MenuType<T>> {
    return key.forRegistration(BuiltInRegistries.MENU) { MenuType(factory, requiredFeatures) } as Lazy<MenuType<T>>
}
//
fun <T : AbstractContainerMenu, D> KambrikAutoRegistrar.forExtendedScreen(
    key: String,
    factory: ExtendedScreenHandlerType.ExtendedFactory<T, D>,
    packetCodec: StreamCodec<FriendlyByteBuf, D>
): Lazy<MenuType<T>> {
    return key.forRegistration(BuiltInRegistries.MENU) { ExtendedScreenHandlerType(factory, packetCodec) } as Lazy<MenuType<T>>
}