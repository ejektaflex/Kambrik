package io.ejekta.kambrik

import io.ejekta.kambrik.api.command.KambrikCommandApi
import io.ejekta.kambrik.api.file.KambrikFileApi
import io.ejekta.kambrik.api.logging.KambrikLoggingApi
import io.ejekta.kambrik.api.message.KambrikMessageApi
import io.ejekta.kambrik.api.structure.KambrikStructureApi
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.server.command.ServerCommandSource
import org.apache.logging.log4j.LogManager


object Kambrik {

    val Command = KambrikCommandApi()

    val Structure = KambrikStructureApi()

    val File = KambrikFileApi()

    val Logging = KambrikLoggingApi()

    val Message = KambrikMessageApi()

    internal val Logger = LogManager.getLogger("Kambrik")

}
