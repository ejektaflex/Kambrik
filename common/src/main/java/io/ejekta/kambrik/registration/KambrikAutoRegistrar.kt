package io.ejekta.kambrik.registration

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.Identifier
import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.percale.toCodec
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.minecraft.stats.Stat
import net.minecraft.stats.StatFormatter
import net.minecraft.stats.Stats
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.npc.VillagerType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.carver.CarverConfiguration
import net.minecraft.world.level.levelgen.carver.WorldCarver
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration


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

    infix fun String.forItem(item: () -> Item) = forRegistration(BuiltInRegistries.ITEM, item)

    infix fun String.forBlock(block: () -> Block) = forRegistration(BuiltInRegistries.BLOCK, block)

    infix fun <C : CarverConfiguration?> String.forCarver(carver: () -> WorldCarver<C>): WorldCarver<C> = forRegistration(BuiltInRegistries.CARVER, carver) as WorldCarver<C>

    infix fun <FC : FeatureConfiguration?> String.forFeature(feature: () -> Feature<FC>): () -> Feature<FC> =
        forRegistration(BuiltInRegistries.FEATURE, feature) as () -> Feature<FC>

    infix fun String.forEffect(status: () -> MobEffect) =
        forRegistration(BuiltInRegistries.MOB_EFFECT, status)

    infix fun String.forAttribute(attribute: () -> Attribute) =
        forRegistration(BuiltInRegistries.ATTRIBUTE, attribute)

    infix fun String.forPotion(potion: () -> Potion) =
        forRegistration(BuiltInRegistries.POTION, potion)

    infix fun <PO : ParticleOptions> String.forParticle(particle: () -> ParticleType<PO>) =
        forRegistration(BuiltInRegistries.PARTICLE_TYPE, particle)

    infix fun String.forVillagerProfession(profession: () -> VillagerProfession) =
        forRegistration(BuiltInRegistries.VILLAGER_PROFESSION, profession)

    infix fun <T : Entity> String.forEntityType(type: () -> EntityType<T>): Lazy<EntityType<T>> =
        forRegistration(BuiltInRegistries.ENTITY_TYPE, type) as Lazy<EntityType<T>>

    infix fun String.forVillagerType(type: () -> VillagerType) =
        forRegistration(BuiltInRegistries.VILLAGER_TYPE, type)

    infix fun String.forSoundEvent(event: () -> SoundEvent) =
        forRegistration(BuiltInRegistries.SOUND_EVENT, event)

    fun <T : BlockEntity> String.forBlockEntity(block: Lazy<Block>, factory: (pos: BlockPos, state: BlockState) -> T): Lazy<BlockEntityType<T>> {
        return forRegistration(BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            BlockEntityType.Builder.of(factory, block.value).build(null)
        } as Lazy<BlockEntityType<T>>
    }

    infix fun <T : AbstractContainerMenu> String.forScreen(menuConstructor: (containerId: Int, inv: Inventory) -> T): Lazy<MenuType<T>> {
        return forRegistration(BuiltInRegistries.MENU) { MenuType(menuConstructor, FeatureFlags.VANILLA_SET) } as Lazy<MenuType<T>>
    }

    infix fun <T : CriterionTrigger<*>> String.forCriterionTrigger(criterion: () -> T): Lazy<T> =
        forRegistration(BuiltInRegistries.TRIGGER_TYPES, criterion) as Lazy<T>

    infix fun String.forStat(formatter: StatFormatter): Lazy<Stat<*>> {
        val statId = Identifier(getId(), this)
        val resultId = forRegistration(BuiltInRegistries.CUSTOM_STAT) { statId }
        return lazy { Stats.CUSTOM.get(resultId.value, formatter) }
    }

    fun <C : Any> String.forComponent(
        serializer: KSerializer<C>,
        serializersModule: SerializersModule = EmptySerializersModule()
    ): Lazy<DataComponentType<C>> {
        return forComponent {
            DataComponentType.builder<C>().persistent(serializer.toCodec(serializersModule)).build()
        }
    }

    infix fun <C : Any> String.forComponent(component: () -> DataComponentType<C>): Lazy<DataComponentType<C>> {
        return forRegistration(BuiltInRegistries.DATA_COMPONENT_TYPE) { component() } as Lazy<DataComponentType<C>>
    }

    companion object {
        inline fun <reified C : Any> KambrikAutoRegistrar.serialComponent(itemId: String): Lazy<DataComponentType<C>> {
            return KambrikRegistrar.register(this, BuiltInRegistries.DATA_COMPONENT_TYPE, itemId, lazy { DataComponentType
                .Builder<C>()
                .persistent(serializer<C>().toCodec(Kambrik.Serial.DefaultSerializers))
                .build() }) as Lazy<DataComponentType<C>>
        }
    }

}

