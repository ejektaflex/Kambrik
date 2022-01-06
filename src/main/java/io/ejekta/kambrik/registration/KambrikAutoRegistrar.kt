package io.ejekta.kambrik.registration

import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.particle.Particle
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.Item
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.potion.Potion
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.village.VillagerProfession
import net.minecraft.village.VillagerType
import net.minecraft.world.gen.carver.Carver
import net.minecraft.world.gen.carver.CarverConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig


@Suppress("UNCHECKED_CAST")
interface KambrikAutoRegistrar : KambrikMarker {

    /**
     * Any non-automatic registration that still needs to be done can
     * be put inside of these methods method, if desired.
     */
    fun beforeRegistration() {}

    fun afterRegistration() {}

    @Deprecated("Phased out in favor of before/after registration methods", ReplaceWith("KambrikAutoRegistrar::afterRegistration"), DeprecationLevel.ERROR)
    fun manualRegister() {}

    fun <T> String.forRegistration(reg: Registry<T>, obj: T): T {
        return KambrikRegistrar.register(this@KambrikAutoRegistrar, reg, this, obj)
    }

    infix fun String.forItem(item: Item): Item = forRegistration(Registry.ITEM, item)

    infix fun String.forBlock(block: Block): Block = forRegistration(Registry.BLOCK, block)

    infix fun String.forEnchant(enchant: Enchantment): Enchantment = forRegistration(Registry.ENCHANTMENT, enchant)

    infix fun <C : CarverConfig?> String.forCarver(carver: Carver<C>): Carver<C> = forRegistration(Registry.CARVER, carver) as Carver<C>

    infix fun <FC : FeatureConfig?> String.forFeature(feature: Feature<FC>): Feature<FC> = forRegistration(Registry.FEATURE, feature) as Feature<FC>

    infix fun String.forStat(statIdentifier: Identifier): Identifier = forRegistration(Registry.CUSTOM_STAT, statIdentifier)

    infix fun String.forStatusEffect(status: StatusEffect): StatusEffect = forRegistration(Registry.STATUS_EFFECT, status)

    infix fun String.forAttribute(attribute: EntityAttribute): EntityAttribute = forRegistration(Registry.ATTRIBUTE, attribute)

    infix fun String.forPotion(potion: Potion): Potion = forRegistration(Registry.POTION, potion)

    infix fun <PE : ParticleEffect> String.forParticle(particle: ParticleType<PE>) = forRegistration(Registry.PARTICLE_TYPE, particle)

    infix fun <R : Registry<*>>String.forVillagerProfession(profession: VillagerProfession) = forRegistration(Registry.VILLAGER_PROFESSION, profession)

    infix fun <T : Entity> String.forEntityType(type: EntityType<T>): EntityType<T> = forRegistration(Registry.ENTITY_TYPE, type) as EntityType<T>

    infix fun String.forVillagerType(type: VillagerType): VillagerType = forRegistration(Registry.VILLAGER_TYPE, type)

    infix fun String.forSoundEvent(event: SoundEvent): SoundEvent = forRegistration(Registry.SOUND_EVENT, event)

    fun <T : BlockEntity>String.forBlockEntity(block: Block, factory: (pos: BlockPos, state: BlockState) -> T): BlockEntityType<T>? {
        return BlockEntityType.Builder.create(factory, block).build(null).also {
            forRegistration(Registry.BLOCK_ENTITY_TYPE, it)
        }
    }

    fun <T : ScreenHandler> forExtendedScreen(id: Identifier, factory: ScreenHandlerRegistry.ExtendedClientHandlerFactory<T>): ScreenHandlerType<T>? {
        return ScreenHandlerRegistry.registerExtended(id, factory)
    }



}