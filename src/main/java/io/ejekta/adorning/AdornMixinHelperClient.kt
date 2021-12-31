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
import java.util.*

object AdornMixinHelperClient {

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
        if (!itemStack.isEmpty && itemStack.item is ArmorItem && itemStack.hasNbt()
            && itemStack.nbt!!.contains("_adornment")
        ) {
            val adornment = Adornments.REGISTRY[Identifier(itemStack.nbt!!.getString("_adornment"))]
            if (adornment != null) {
                val armorItem = itemStack.item as ArmorItem
                val st: ItemStack = stacks[armorItem.slotType] ?: return
                val bakedModel: BakedModel = MinecraftClient.getInstance().itemRenderer
                    .getModel(st, null, null, 0)
                renderItem(st, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel, adornment.colour)
            }
        }
    }

    private val stacks = mapOf(
        EquipmentSlot.HEAD to ItemStack(AdornmentMod.DISPLAY_ITEM_HELMET),
        EquipmentSlot.CHEST to ItemStack(AdornmentMod.DISPLAY_ITEM_CHEST),
        EquipmentSlot.LEGS to ItemStack(AdornmentMod.DISPLAY_ITEM_LEGGINGS),
        EquipmentSlot.FEET to ItemStack(AdornmentMod.DISPLAY_ITEM_FEET)
    )

    private fun renderItem(
        stack: ItemStack,
        renderMode: ModelTransformation.Mode,
        leftHanded: Boolean,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int,
        model: BakedModel,
        color: Int
    ) {
        if (!stack.isEmpty) {
            matrices.push()
            val bl = renderMode == ModelTransformation.Mode.GUI
            val bl2 = bl || renderMode == ModelTransformation.Mode.GROUND || renderMode == ModelTransformation.Mode.FIXED
            model.transformation.getTransformation(renderMode).apply(leftHanded, matrices)
            matrices.translate(-0.5, -0.5, -0.5)
            if (!model.isBuiltin && (stack.item !== Items.TRIDENT || bl2)) {
                val idk = renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || renderMode == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND || renderMode == ModelTransformation.Mode.FIXED
                val renderLayer = RenderLayers.getItemLayer(stack, idk)
                val vertexConsumer: VertexConsumer =
                    ItemRenderer.getArmorGlintConsumer(vertexConsumers, renderLayer, false, stack.hasGlint())
                renderBakedItemModel(model, light, overlay, matrices, vertexConsumer, color)
            }
            matrices.pop()
        }
    }

    private fun renderBakedItemModel(
        model: BakedModel,
        light: Int,
        overlay: Int,
        matrices: MatrixStack,
        vertices: VertexConsumer,
        color: Int
    ) {
        val random = Random()
        val dirs = Direction.values()
        for (direction in dirs) {
            random.setSeed(42L)
            renderBakedItemQuads(matrices, vertices, model.getQuads(null, direction, random), light, overlay, color)
        }
        random.setSeed(42L)
        renderBakedItemQuads(matrices, vertices, model.getQuads(null, null, random), light, overlay, color)
    }

    private fun renderBakedItemQuads(
        matrices: MatrixStack,
        vertices: VertexConsumer,
        quads: List<BakedQuad>,
        light: Int,
        overlay: Int,
        color: Int
    ) {
        val entry = matrices.peek()
        for (bakedQuad in quads) {
            val r = (color shr 16 and 255).toFloat() / 255.0f
            val g = (color shr 8 and 255).toFloat() / 255.0f
            val b = (color and 255).toFloat() / 255.0f
            vertices.quad(entry, bakedQuad, r, g, b, light, overlay)
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