package io.ejekta.kambrik.registration

import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.minecraft.advancement.criterion.Criterion
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.Item
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.potion.Potion
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.Stat
import net.minecraft.stat.StatFormatter
import net.minecraft.stat.Stats
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.village.VillagerProfession
import net.minecraft.village.VillagerType
import net.minecraft.world.gen.carver.Carver
import net.minecraft.world.gen.carver.CarverConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig


@Suppress("UNCHECKED_CAST")
interface KambrikAutoRegistrar : KambrikMarker {

    fun getId(): String

    /**
     * Any non-automatic registration that still needs to be done can
     * be put inside of these methods, if desired.
     */
    fun beforeRegistration() {}

    fun afterRegistration() {}


    fun <T> String.forRegistration(reg: Registry<T>, obj: () -> T): Lazy<T> {
        return KambrikRegistrar.register(this@KambrikAutoRegistrar, reg, this, lazy(obj))
    }

    infix fun String.forItem(item: () -> Item) = forRegistration(Registries.ITEM, item)

    infix fun String.forBlock(block: () -> Block) = forRegistration(Registries.BLOCK, block)

    infix fun String.forEnchant(enchant: () -> Enchantment) = forRegistration(Registries.ENCHANTMENT, enchant)

    infix fun <C : CarverConfig?> String.forCarver(carver: () -> Carver<C>): Carver<C> = forRegistration(Registries.CARVER, carver) as Carver<C>

    infix fun <FC : FeatureConfig?> String.forFeature(feature: () -> Feature<FC>): () -> Feature<FC> = forRegistration(Registries.FEATURE, feature) as () -> Feature<FC>

    infix fun String.forStatusEffect(status: () -> StatusEffect) = forRegistration(Registries.STATUS_EFFECT, status)

    infix fun String.forAttribute(attribute: () -> EntityAttribute) = forRegistration(Registries.ATTRIBUTE, attribute)

    infix fun String.forPotion(potion: () -> Potion) = forRegistration(Registries.POTION, potion)

    infix fun <PE : ParticleEffect> String.forParticle(particle: () -> ParticleType<PE>) = forRegistration(Registries.PARTICLE_TYPE, particle)

    infix fun String.forVillagerProfession(profession: () -> VillagerProfession) = forRegistration(Registries.VILLAGER_PROFESSION, profession)

    infix fun <T : Entity> String.forEntityType(type: () -> EntityType<T>): Lazy<EntityType<T>> = forRegistration(Registries.ENTITY_TYPE, type) as Lazy<EntityType<T>>

    infix fun String.forVillagerType(type: () -> VillagerType) = forRegistration(Registries.VILLAGER_TYPE, type)

    infix fun String.forSoundEvent(event: () -> SoundEvent) = forRegistration(Registries.SOUND_EVENT, event)

    fun <T : BlockEntity> String.forBlockEntity(block: Lazy<Block>, factory: (pos: BlockPos, state: BlockState) -> T): Lazy<BlockEntityType<T>> {
        return forRegistration(Registries.BLOCK_ENTITY_TYPE) {
            BlockEntityType.Builder.create(factory, block.value).build(null)
        } as Lazy<BlockEntityType<T>>
    }

    infix fun <T : ScreenHandler> String.forScreen(factory: ScreenHandlerType.Factory<T>): Lazy<ScreenHandlerType<T>> {
        return forRegistration(Registries.SCREEN_HANDLER) {
            ScreenHandlerType(
                factory,
                FeatureFlags.VANILLA_FEATURES
            )
        } as Lazy<ScreenHandlerType<T>>
    }

    infix fun <T : Criterion<*>> String.forCriterion(criterion: () -> T): Lazy<T> = forRegistration(Registries.CRITERION, criterion) as Lazy<T>

    infix fun String.forStat(formatter: StatFormatter): Lazy<Stat<*>> {
        val statId = Identifier(getId(), this)
        val resultId = forRegistration(Registries.CUSTOM_STAT) { statId }
        return lazy { Stats.CUSTOM.getOrCreateStat(resultId.value, formatter) }
    }

}