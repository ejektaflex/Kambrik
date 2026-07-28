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
typealias GsonObject = com.google.gson.JsonObject

/**
 * Accessed via [Kambrik.Criterion][io.ejekta.kambrik.Kambrik.Criterion]
 */
class KambrikCriterionApi internal constructor() {

    fun interface KambrikCriterionSubscriber {
        fun handle(player: ServerPlayer, criterion: SimpleTrigger, predicate: SimpleTriggerPredicate)
    }

    private val handlers = mutableListOf<Pair<SimpleTriggerInstance, ServerPlayer.() -> Unit>>()

    private val subscribers = mutableListOf<KambrikCriterionSubscriber>()

    fun handleGameTrigger(player: ServerPlayer, criterion: SimpleTrigger, predicate: SimpleTriggerPredicate) {
        // The predicate handed to us belongs to the criterion that actually fired, and it usually
        // closes over a context object of a type only that criterion understands. Subscribers and
        // handlers legitimately test it against trigger instances parsed from elsewhere (data packs,
        // bounties, quests), which for strongly-typed criteria blows up with a ClassCastException
        // deep inside the other mod's `matches`. A mismatch just means "this instance is not the one
        // that fired", so treat it as a non-match instead of letting it kill the server tick.
        // Reported against Cobblemon's CaughtPokemonCriterion, but any mod with typed criterion
        // contexts hits it.
        val safePredicate = Predicate<SimpleTriggerInstance> { instance ->
            try {
                predicate.test(instance)
            } catch (e: ClassCastException) {
                false
            }
        }
        for (subscriber in subscribers) {
            subscriber.handle(player, criterion, safePredicate)
        }
        for ((condition, func) in handlers) {
            val result = testAgainst(criterion, condition, safePredicate)
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
        return createCriterionConditionsFromGson(gsonData)
    }

    fun createCriterionConditionsFromGson(gsonCriterion: GsonObject): SimpleTriggerInstance? {
        return try {
            val abc = Criterion.CODEC.decode(JsonOps.INSTANCE, gsonCriterion)
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