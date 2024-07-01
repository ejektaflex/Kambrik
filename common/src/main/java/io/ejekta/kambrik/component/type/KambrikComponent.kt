package io.ejekta.kambrik.component.type

import com.mojang.datafixers.util.Function6
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.serializer
import kotlinx.serialization.serializerOrNull
import net.minecraft.component.Component
import net.minecraft.component.type.FoodComponent
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextType
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

class KambrikComponent<S : Any>(klass: KClass<S>, data: S) {

    companion object {

        @Serializable
        class TestComponent(val data: String = "test_data")

        @JvmRecord
        data class BDE(
            val id: String,
            val logicId: String,
            val content: String,
            val amount: Int,
            val components: List<Component<*>>,
            val name: String? = null,
            val icon: Identifier? = null,
            val isMystery: Boolean = false,
//            val rarity: BountyRarity = BountyRarity.COMMON,
//            val tracking: JsonObject = JsonObject(emptyMap()), // Used to track extra data, e.g. current progress if needed
//            val critConditions: JsonObject? = null,
            val current: Int = 0, // Current progress
            val relatedDecreeIds: Set<String> = emptySet()
        )

        val BDE_CODEC = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<BDE> ->
            instance.group(
                Codec.STRING.fieldOf("id").forGetter(BDE::id),
                Codec.STRING.fieldOf("logicId").forGetter(BDE::logicId),
                Codec.STRING.fieldOf("content").forGetter(BDE::content),
                Codec.INT.fieldOf("amount").forGetter(BDE::amount),
                // Components??
                Codec.STRING.optionalFieldOf("name").forGetter { obj: BDE -> Optional.ofNullable(obj.name) },
                Codecs.IDENTIFIER_PATH.optionalFieldOf("icon").forGetter { obj: BDE -> Optional.ofNullable(obj.icon?.toString()) },
                Codec.BOOL.fieldOf("isMystery").forGetter(BDE::isMystery),
                Codecs.POSITIVE_INT.fieldOf("current").forGetter(BDE::current),
                Codec.STRING.listOf().fieldOf("relatedDecrees").forGetter { obj: BDE -> obj.relatedDecreeIds.toList() }
            ).apply(instance) { id, logicId, content, amount, /* components */ name, icon, isMystery, current, relatedDecrees ->
                BDE(id, logicId, content, amount, /* comps */
                    emptyList(), name.getOrNull(), icon.getOrNull()?.let { Identifier.of(it) }, isMystery, current, relatedDecrees.toSet())
            }
        }



        val TEST = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<TestComponent> ->
            instance.group(Codec.STRING.fieldOf("data").forGetter { obj: TestComponent -> obj.data })
                .apply(instance) { data: String -> TestComponent(data) }
        }

    }
}