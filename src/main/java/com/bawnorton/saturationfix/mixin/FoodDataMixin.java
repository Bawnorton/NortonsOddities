package com.bawnorton.saturationfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Unique
    private final ThreadLocal<Boolean> saturationfix$didHeal = ThreadLocal.withInitial(() -> false);

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
    private void onHeal(Player instance, float v, Operation<Void> original) {
        float preHealth = instance.getHealth();
        original.call(instance, v);
        float postHealth = instance.getHealth();
        saturationfix$didHeal.set(preHealth < postHealth);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private void onAddExhaustion(FoodData instance, float v, Operation<Void> original) {
        if (saturationfix$didHeal.get()) {
            original.call(instance, v);
            saturationfix$didHeal.set(false);
        }
    }
}
