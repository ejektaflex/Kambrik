package io.ejekta.adorning

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ArmorItem
import net.minecraft.util.Identifier

object AdornMixinHelper {

    fun <T : LivingEntity, A : BipedEntityModel<T>> renderArmor(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        entity: T,
        slot: EquipmentSlot,
        light: Int,
        armorModel: A,
        useFunc: (mdl: A, slt: EquipmentSlot) -> Boolean
    ) {
        val itemStack = entity.getEquippedStack(slot)
        if (itemStack.item is ArmorItem) {
            val armorItem = itemStack.item as ArmorItem
            if (itemStack.hasNbt() && itemStack.nbt!!.contains("_adornment")) {
                val adornment = Adornments.REGISTRY[Identifier(itemStack.getNbt()!!.getString("_adornment"))]
                if (adornment != null && armorItem.slotType == slot) {
                    val bl = useFunc(armorModel, slot)
                    val bl2 = itemStack.hasGlint()
                    val color: Int = adornment.colour
                    val r = (color shr 16 and 255).toFloat() / 255.0f
                    val g = (color shr 8 and 255).toFloat() / 255.0f
                    val b = (color and 255).toFloat() / 255.0f
                    this.renderArmorParts(matrices, vertexConsumers, light, bl2, armorModel, bl, r, g, b)
                }
            }
        }
    }

    private fun renderArmorParts(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        glint: Boolean,
        armorModel: BipedEntityModel<*>,
        secondLayer: Boolean,
        red: Float,
        green: Float,
        blue: Float
    ) {
        val vertexConsumer = ItemRenderer.getArmorGlintConsumer(
            vertexConsumers,
            RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(secondLayer)),
            false,
            glint
        )
        armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0f)
    }

    fun getArmorTexture(secondLayer: Boolean): Identifier {
        val string = "textures/adornment/adornment_" + (if (secondLayer) 2 else 1) + ".png"
        return Identifier(AdornmentMod.ID, string)
    }

}