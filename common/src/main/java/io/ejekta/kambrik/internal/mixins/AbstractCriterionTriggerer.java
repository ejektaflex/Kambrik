package io.ejekta.kambrik.internal.mixins;

import io.ejekta.kambrik.Kambrik;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(SimpleCriterionTrigger.class)
public class AbstractCriterionTriggerer<T extends SimpleCriterionTrigger.SimpleInstance> {
    @Inject(method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Predicate;)V",
            at = @At("HEAD")
    )
    private void injected_kambrik(ServerPlayer player, Predicate<T> predicate, CallbackInfo ci) {
        Kambrik.INSTANCE.getCriterion().handleGameTrigger(
                player,
                (SimpleCriterionTrigger<SimpleCriterionTrigger.SimpleInstance>)(Object)this,
                (Predicate<SimpleCriterionTrigger.SimpleInstance>) predicate
        );
    }
}

