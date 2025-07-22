package com.bawnorton.sporeadditions.mixin;

import com.Harbinger.Spore.Compat.SurgeryCraftingCategory;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SurgeryCraftingCategory.class)
public abstract class SurgeryCraftingCategoryMixin {
    @Shadow public Ingredient stiches;

    @Shadow @Final public TagKey<Item> stringLikeItem;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void useAProperIngredient(IGuiHelper helper, CallbackInfo ci) {
        stiches = Ingredient.of(ForgeRegistries.ITEMS.tags().getTag(stringLikeItem).stream().map(Item::getDefaultInstance));
    }
}
