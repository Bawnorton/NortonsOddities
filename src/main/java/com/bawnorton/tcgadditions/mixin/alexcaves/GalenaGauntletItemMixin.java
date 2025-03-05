package com.bawnorton.tcgadditions.mixin.alexcaves;

import com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

@Mixin(GalenaGauntletItem.class)
public abstract class GalenaGauntletItemMixin {
    @WrapOperation(
            method = {
                    "use",
                    "onUseTick"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean orIsTinkersWithMetalic(ItemStack instance, TagKey<Item> pTag, Operation<Boolean> original, @Local(name = "crystallization") boolean isCrystallization) {
        boolean result = original.call(instance, pTag);
        if (isCrystallization || result) return true;

        if(instance.getItem() instanceof IModifiable) {
            ToolStack stack = ToolStack.from(instance);
            ModifierEntry entry = stack.getModifier(TinkerModifiers.magnetic.get());
            return entry != ModifierEntry.EMPTY;
        }
        return false;
    }
}
