package io.ejekta.kambrikx.data.config

import io.ejekta.kambrikx.data.DataProperty
import kotlinx.serialization.KSerializer
import net.minecraft.util.Identifier

class ConfigDataProperty<T : Any>(
    key: Identifier,
    default: () -> T,
    serializer: KSerializer<T>
) : DataProperty<T>(key, default, serializer, ConfigDataRegistrar)