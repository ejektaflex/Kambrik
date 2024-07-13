package io.ejekta.kambrik.fabric.registration

import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.registry.Registries
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

fun <T : ScreenHandler> KambrikAutoRegistrar.forScreen(key: String, factory: ScreenHandlerType.Factory<T>, requiredFeatures: FeatureSet): Lazy<ScreenHandlerType<T>> {
    return key.forRegistration(Registries.SCREEN_HANDLER) { ScreenHandlerType(factory, requiredFeatures) } as Lazy<ScreenHandlerType<T>>
}
//
fun <T : ScreenHandler, D> KambrikAutoRegistrar.forExtendedScreen(
    key: String,
    factory: ExtendedScreenHandlerType.ExtendedFactory<T, D>,
    packetCodec: PacketCodec<RegistryByteBuf, D>
): Lazy<ScreenHandlerType<T>> {
    return key.forRegistration(Registries.SCREEN_HANDLER) { ExtendedScreenHandlerType(factory, packetCodec) } as Lazy<ScreenHandlerType<T>>
}