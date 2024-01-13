package io.ejekta.kambrik.internal.mixins;

import io.ejekta.kambrik.Kambrik;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(AbstractCriterion.class)
public class AbstractCriterionTriggerer<T extends AbstractCriterion.Conditions> {
    @Inject(method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Predicate;)V",
            at = @At("HEAD")
    )
    private void injected(ServerPlayerEntity player, Predicate<T> predicate, CallbackInfo ci) {
        Kambrik.INSTANCE.getCriterion().handleGameTrigger$Kambrik(
                player,
                (AbstractCriterion<AbstractCriterion.Conditions>)(Object)this,
                (Predicate<AbstractCriterion.Conditions>) predicate
        );
    }
}

