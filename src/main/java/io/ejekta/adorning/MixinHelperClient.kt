package io.ejekta.adorning

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*

object MixinHelperClient {

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

    fun onItemRender(
        itemStack: ItemStack,
        renderMode: ModelTransformation.Mode,
        leftHanded: Boolean,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int,
        model: BakedModel?
    ) {
        if (!itemStack.isEmpty && itemStack.item is ArmorItem && itemStack.hasNbt() && itemStack.nbt!!.contains("_adornment")
        ) {
            val adornment = Adornments.REGISTRY[Identifier(itemStack.nbt!!.getString("_adornment"))]
            if (adornment != null) {
                val armorItem = itemStack.item as ArmorItem
                val st: ItemStack = (stacks[armorItem.slotType] ?: return).apply {
                    nbt = itemStack.nbt
                }
                val bakedModel: BakedModel = MinecraftClient.getInstance().itemRenderer
                    .getModel(st, null, null, 0)
                MinecraftClient.getInstance().itemRenderer.renderItem(st, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel)
            }
        }
    }

    private val stacks = mapOf(
        EquipmentSlot.HEAD to ItemStack(AdornmentMod.DISPLAY_ITEM_HELMET),
        EquipmentSlot.CHEST to ItemStack(AdornmentMod.DISPLAY_ITEM_CHEST),
        EquipmentSlot.LEGS to ItemStack(AdornmentMod.DISPLAY_ITEM_LEGGINGS),
        EquipmentSlot.FEET to ItemStack(AdornmentMod.DISPLAY_ITEM_FEET)
    )

    fun onRenderBakedItemQuads(
        matrices: MatrixStack,
        vertices: VertexConsumer,
        quads: List<BakedQuad>,
        light: Int,
        overlay: Int,
        stack: ItemStack,
        ci: CallbackInfo
    ) {
        if (!stack.isEmpty && stack.item is AdornmentDisplay) {
            val adornment = Adornments.REGISTRY[Identifier(stack.orCreateNbt.getString("_adornment"))] ?: Adornments.DIAMOND
            val color = adornment.colour
            val entry = matrices.peek()
            for (bakedQuad in quads) {
                val r = (color shr 16 and 255).toFloat() / 255.0f
                val g = (color shr 8 and 255).toFloat() / 255.0f
                val b = (color and 255).toFloat() / 255.0f
                vertices.quad(entry, bakedQuad, r, g, b, light, overlay)
            }
            ci.cancel()
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
            RenderLayer.getArmorCutoutNoCull(getArmorTexture(secondLayer)),
            false,
            glint
        )
        armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0f)
    }

    private fun getArmorTexture(secondLayer: Boolean): Identifier {
        val string = "textures/adornment/adornment_" + (if (secondLayer) 2 else 1) + ".png"
        return Identifier(AdornmentMod.ID, string)
    }

}