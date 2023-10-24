package io.ejekta.kambrik.criterion

import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.JsonHelper
import java.util.function.Predicate
import kotlin.reflect.full.isSubclassOf


/**
 * Accessed via [Kambrik.Criterion][io.ejekta.kambrik.Kambrik.Criterion]
 */
class KambrikCriterionApi internal constructor() {

    fun interface KambrikCriterionSubscriber {
        fun handle(player: ServerPlayerEntity, criterion: AbstractCriterion<AbstractCriterionConditions>, predicate: Predicate<AbstractCriterionConditions>)
    }

    private val predicateDeserializer = AdvancementEntityPredicateDeserializer(
        Kambrik.idOf("advancement_entity_predicate_deserializer"), null
    )

    private val handlers = mutableListOf<Pair<AbstractCriterionConditions, ServerPlayerEntity.() -> Unit>>()

    private val subscribers = mutableListOf<KambrikCriterionSubscriber>()

    internal fun handleGameTrigger(player: ServerPlayerEntity, criterion: AbstractCriterion<AbstractCriterionConditions>, predicate: Predicate<AbstractCriterionConditions>) {
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

    fun addCriterionHandler(absCond: AbstractCriterionConditions, func: ServerPlayerEntity.() -> Unit) {
        handlers.add(absCond to func)
    }

    fun createCriterionConditionsFromJson(jsonCriterion: JsonObject): AbstractCriterionConditions? {
        val gsonData = JsonHelper.deserialize(jsonCriterion.toString()) // KSX Json to GSON Json
        return try {
            AdvancementCriterion.fromJson(gsonData, predicateDeserializer).conditions as AbstractCriterionConditions
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun <T : AbstractCriterionConditions> testAgainst(criterion: AbstractCriterion<T>, conditions: AbstractCriterionConditions, predicate: Predicate<T>): Boolean {
        // If the criterion we hooked into has the same ID as our Json criterion, then test
        if (conditions::class.isSubclassOf(criterion::class)) {
            @Suppress("UNCHECKED_CAST")
            return predicate.test(conditions as T)
        }
        return false
    }

}