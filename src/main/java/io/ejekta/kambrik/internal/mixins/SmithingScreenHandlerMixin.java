package io.ejekta.kambrik.internal.mixins;

import io.ejekta.adorning.MixinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
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

    ItemStack result;

    @Inject(at = @At("RETURN"), method = "canTakeOutput(Lnet/minecraft/entity/player/PlayerEntity;Z)Z", cancellable = true)
    protected void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cbi)
    {
        if (result != null) {
            cbi.setReturnValue(true);
        }
    }

    @Inject(at = @At("TAIL"), method = "onTakeOutput(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", cancellable = true)
    public void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        result = null;
    }

    @Inject(at = @At("TAIL"), method = "updateResult()V")
    public void updateResult(CallbackInfo cbi)
    {
        if(!output.isEmpty()) {
            return;
        }

        try {
            result = MixinHelper.INSTANCE.smithingCanTake(
                    this.input.getStack(0),
                    this.input.getStack(1)
            );
            System.out.println("Result gotten. ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != null) {
            output.setStack(0, result);
        }
    }
}
