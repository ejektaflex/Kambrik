package io.ejekta.kambrik.internal.mixins;

import io.ejekta.adorning.Adornment;
import io.ejekta.adorning.Adornments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler
{
    public SmithingScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context)
    {
        super(type, syncId, playerInventory, context);
    }

    @Inject(at = @At("RETURN"), method = "canTakeOutput(Lnet/minecraft/entity/player/PlayerEntity;Z)Z", cancellable = true)
    protected void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cbi)
    {
        ItemStack armourStack = this.input.getStack(0);
        ItemStack materialStack = this.input.getStack(1);
        Adornment materialAdornment = Adornments.INSTANCE.getREGISTRY().getForMaterial(materialStack.getItem());

        if(!armourStack.isEmpty() && armourStack.getItem() instanceof ArmorItem)
        {
            if(materialAdornment != null)
            {
                ArmorItem armorItem = (ArmorItem) armourStack.getItem();
                if(armorItem.getMaterial().getRepairIngredient().test(materialStack)) return;
                if(armourStack.hasNbt() && armourStack.getNbt().contains("_adornment")) return;

                cbi.setReturnValue(true);
            }
            else
            {
                if(armourStack.hasNbt() && armourStack.getNbt().contains("_adornment"))
                {
                    cbi.setReturnValue(true);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "updateResult()V")
    public void updateResult(CallbackInfo cbi)
    {
        if(!output.isEmpty()) {
            return;
        }

        ItemStack armourStack = this.input.getStack(0);
        ItemStack materialStack = this.input.getStack(1);

        Adornment adornment = Adornments.INSTANCE.getREGISTRY().getForMaterial(materialStack.getItem());
        if(!armourStack.isEmpty() && armourStack.getItem() instanceof ArmorItem)
        {
            ArmorItem armorItem = (ArmorItem) armourStack.getItem();
            ItemStack outputStack = ItemStack.EMPTY;

            if(adornment != null)
            {
                if(armorItem.getMaterial().getRepairIngredient().test(materialStack)) return;

                if(armourStack.hasNbt() && armourStack.getNbt().contains("_adornment")) return;

                outputStack = new ItemStack(armorItem);
                NbtCompound origTag = armourStack.getNbt();
                NbtCompound newTag;
                if(origTag == null) newTag = new NbtCompound();
                else newTag = origTag.copy();

                newTag.putString("_adornment", Adornments.INSTANCE.getREGISTRY().getId(adornment).toString());
                outputStack.setNbt(newTag);
            }
            else if(materialStack.isEmpty())
            {
                if(armourStack.hasNbt() && armourStack.getNbt().contains("_adornment"))
                {
                    outputStack = armourStack.copy();
                    outputStack.getNbt().remove("_adornment");
                }
            }
            output.setStack(0, outputStack);
        }
    }
}
