package com.bawnorton.taf.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;
import shadows.apotheosis.core.attributeslib.impl.AttributeEvents;

@Mixin(AttributeEvents.class)
public abstract class AttributeEventsMixin {
    @ModifyReturnValue(
            method = "canBenefitFromDrawSpeed",
            at = @At("RETURN"),
            remap = false
    )
    private boolean canBenefitFromDrawSpeed(boolean original, ItemStack stack) {
        if(original) return true;

        return stack.getItem() instanceof ModularBowItem || stack.getItem() instanceof ModularCrossbowItem;
    }
}
