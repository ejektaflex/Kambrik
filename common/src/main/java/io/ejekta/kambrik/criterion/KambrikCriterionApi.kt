package io.ejekta.kambrik.criterion

import com.mojang.serialization.JsonOps
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.GsonHelper
import java.util.function.Predicate
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.full.isSubclassOf

typealias SimpleTriggerInstance = SimpleCriterionTrigger.SimpleInstance
typealias SimpleTrigger = SimpleCriterionTrigger<SimpleTriggerInstance>
typealias SimpleTriggerPredicate = Predicate<SimpleTriggerInstance>

/**
 * Accessed via [Kambrik.Criterion][io.ejekta.kambrik.Kambrik.Criterion]
 */
class KambrikCriterionApi internal constructor() {

    fun interface KambrikCriterionSubscriber {
        fun handle(player: ServerPlayer, criterion: SimpleTrigger, predicate: SimpleTriggerPredicate)
    }

    private val handlers = mutableListOf<Pair<SimpleTriggerInstance, ServerPlayer.() -> Unit>>()

    private val subscribers = mutableListOf<KambrikCriterionSubscriber>()

    internal fun handleGameTrigger(player: ServerPlayer, criterion: SimpleTrigger, predicate: SimpleTriggerPredicate) {
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

    fun addCriterionHandler(jsonString: String, func: ServerPlayer.() -> Unit) {
        val absCond = createCriterionConditionsFromJson(
            Json.decodeFromString(JsonObject.serializer(), jsonString)
        )
        addCriterionHandler(absCond ?: return, func)
    }

    fun addCriterionHandler(jsonObject: JsonObject, func: ServerPlayer.() -> Unit) {
        val absCond = createCriterionConditionsFromJson(jsonObject)
        addCriterionHandler(absCond ?: return, func)
    }

    fun addCriterionHandler(absCond: SimpleTriggerInstance, func: ServerPlayer.() -> Unit) {
        handlers.add(absCond to func)
    }

    fun createCriterionConditionsFromJson(jsonCriterion: JsonObject): SimpleTriggerInstance? {
        val gsonData = GsonHelper.parse(jsonCriterion.toString()) // KSX Json to GSON Json
        return try {
            //AdvancementCriterion.fromJson(gsonData, predicateDeserializer).conditions as AbstractCriterion.Conditions
            val abc = Criterion.CODEC.decode(JsonOps.INSTANCE, gsonData)
            val res = abc.result().getOrNull()
            res?.first?.triggerInstance as? SimpleTriggerInstance
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun <T : SimpleTriggerInstance> testAgainst(criterion: SimpleCriterionTrigger<T>, conditions: SimpleTriggerInstance, predicate: Predicate<T>): Boolean {
        // If the criterion we hooked into has the same ID as our Json criterion, then test
        if (conditions::class.isSubclassOf(criterion::class)) {
            @Suppress("UNCHECKED_CAST")
            return predicate.test(conditions as T)
        }
        return false
    }

}