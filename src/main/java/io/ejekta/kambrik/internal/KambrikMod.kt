package io.ejekta.kambrik.internal

import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import io.ejekta.kambrikx.data.KambrikPersistence
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager

internal object KambrikMod : ModInitializer {

    const val ID = "kambrik"
    
    val Logger = LogManager.getLogger("Kambrik")

    fun idOf(unique: String) = Identifier(ID, unique)

    override fun onInitialize() {
        // Auto Registration feature
        FabricLoader.getInstance().getEntrypointContainers(ID, KambrikMarker::class.java).forEach {
            Logger.debug("Got mod entrypoint: $it, ${it.entrypoint} from ${it.provider.metadata.id}, will do Kambrik init here")
            KambrikRegistrar.doMainRegistrationFor(it)
        }

        // Kambrik commands
        CommandRegistrationCallback.EVENT.register(KambrikCommands)

        // Server data lifecycle management

        ServerLifecycleEvents.SERVER_STARTED.register {
            KambrikPersistence.loadServerResults()
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            KambrikPersistence.saveAllServerResults()

            if (FabricLoader.getInstance().environmentType != EnvType.CLIENT) {
                KambrikPersistence.saveAllConfigResults()
            }
        }
    }

}


