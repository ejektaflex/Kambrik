package io.ejekta.kambrik.criterion

import com.mojang.serialization.JsonOps
import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.CriterionConditions
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.JsonHelper
import java.util.function.Predicate
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.full.isSubclassOf


/**
 * Accessed via [Kambrik.Criterion][io.ejekta.kambrik.Kambrik.Criterion]
 */
class KambrikCriterionApi internal constructor() {

    fun interface KambrikCriterionSubscriber {
        fun handle(player: ServerPlayerEntity, criterion: AbstractCriterion<AbstractCriterion.Conditions>, predicate: Predicate<AbstractCriterion.Conditions>)
    }

    private val handlers = mutableListOf<Pair<AbstractCriterion.Conditions, ServerPlayerEntity.() -> Unit>>()

    private val subscribers = mutableListOf<KambrikCriterionSubscriber>()

    internal fun handleGameTrigger(player: ServerPlayerEntity, criterion: AbstractCriterion<AbstractCriterion.Conditions>, predicate: Predicate<AbstractCriterion.Conditions>) {
        for (subscriber in subscribers) {
            subscriber.handle(player, criterion, predicate)
        }
        for ((condition, func) in handlers) {
            val result = testAgainst(criterion, condition, predicate)
            if (result) {
                func(player)
            }
        }
    }

    fun subscribe(subscriber: KambrikCriterionSubscriber) {
        subscribers.add(subscriber)
    }

    fun addCriterionHandler(jsonString: String, func: ServerPlayerEntity.() -> Unit) {
        val absCond = createCriterionConditionsFromJson(
            Json.decodeFromString(JsonObject.serializer(), jsonString)
        )
        addCriterionHandler(absCond ?: return, func)
    }

    fun addCriterionHandler(jsonObject: JsonObject, func: ServerPlayerEntity.() -> Unit) {
        val absCond = createCriterionConditionsFromJson(jsonObject)
        addCriterionHandler(absCond ?: return, func)
    }

    fun addCriterionHandler(absCond: AbstractCriterion.Conditions, func: ServerPlayerEntity.() -> Unit) {
        handlers.add(absCond to func)
    }

    fun createCriterionConditionsFromJson(jsonCriterion: JsonObject): AbstractCriterion.Conditions? {
        val gsonData = JsonHelper.deserialize(jsonCriterion.toString()) // KSX Json to GSON Json
        return try {
            //AdvancementCriterion.fromJson(gsonData, predicateDeserializer).conditions as AbstractCriterion.Conditions
            val abc = AdvancementCriterion.CODEC.decode(JsonOps.INSTANCE, gsonData)
            val res = abc.result().getOrNull()
            res?.first?.conditions as? AbstractCriterion.Conditions
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun <T : AbstractCriterion.Conditions> testAgainst(criterion: AbstractCriterion<T>, conditions: AbstractCriterion.Conditions, predicate: Predicate<T>): Boolean {
        // If the criterion we hooked into has the same ID as our Json criterion, then test
        if (conditions::class.isSubclassOf(criterion::class)) {
            @Suppress("UNCHECKED_CAST")
            return predicate.test(conditions as T)
        }
        return false
    }

}