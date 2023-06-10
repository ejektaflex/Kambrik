package io.ejekta.kambrik.registration

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.registry.Registries
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

fun <T : ScreenHandler> KambrikAutoRegistrar.forScreen(key: String, factory: ScreenHandlerType.Factory<T>): ScreenHandlerType<T> {
    return key.forRegistration(Registries.SCREEN_HANDLER, ScreenHandlerType(factory)) as ScreenHandlerType<T>
}

fun <T : ScreenHandler> KambrikAutoRegistrar.forExtendedScreen(key: String, factory: ExtendedScreenHandlerType.ExtendedFactory<T>): ScreenHandlerType<T> {
    return key.forRegistration(Registries.SCREEN_HANDLER, ExtendedScreenHandlerType(factory)) as ScreenHandlerType<T>
}