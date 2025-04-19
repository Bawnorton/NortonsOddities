package com.bawnorton.tcgadditions.mixin.pnc;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.desht.pneumaticcraft.common.block.entity.RangeManager;
import me.desht.pneumaticcraft.common.block.entity.VacuumTrapBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(VacuumTrapBlockEntity.class)
public abstract class VacuumTrapBlockEntityMixin {
    @Shadow @Final private RangeManager rangeManager;

    @ModifyExpressionValue(
            method = "tickServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"
            )
    )
    private double youShouldBeSquareRootingThis(double value) {
        return Math.sqrt(value);
    }

    @ModifyVariable(
            method = "tickServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"
            ),
            name = "min"
    )
    private double applyRangeToMin(double value) {
        return value + rangeManager.getRange() * 2;
    }
}
