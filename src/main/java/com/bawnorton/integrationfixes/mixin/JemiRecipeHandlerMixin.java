package com.bawnorton.integrationfixes.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.jemi.JemiRecipeHandler;
import dev.emi.emi.jemi.impl.JemiRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;

@Mixin(JemiRecipeHandler.class)
public abstract class JemiRecipeHandlerMixin {
    @Inject(
            method = "addIngredients",
            at = @At("HEAD"),
            remap = false
    )
    private void fixIngredientPositioning(JemiRecipeLayoutBuilder builder,
            List<SlotWidget> widgets,
            List<? extends EmiIngredient> stacks,
            RecipeIngredientRole role,
            CallbackInfo ci,
            @Share("consumed") LocalRef<List<SlotWidget>> consumedHolder) {
        consumedHolder.set(new ArrayList<>());
    }

    @WrapOperation(
            method = "addIngredients",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/jemi/impl/JemiRecipeLayoutBuilder;addSlot(Lmezz/jei/api/recipe/RecipeIngredientRole;II)Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;"
            ),
            remap = false
    )
    private IRecipeSlotBuilder positionIngredientCorrectly(JemiRecipeLayoutBuilder instance,
            RecipeIngredientRole recipeIngredientRole,
            int x,
            int y,
            Operation<IRecipeSlotBuilder> original,
            @Local(name = "widgets") List<SlotWidget> widgets,
            @Local(name = "ing") EmiIngredient ing,
            @Share("consumed") LocalRef<List<SlotWidget>> consumedHolder) {
        if(x == 0 && y == 0 && !widgets.isEmpty()) {
            List<SlotWidget> consumed = consumedHolder.get();
            List<SlotWidget> toCheck = new ArrayList<>(widgets);
            toCheck.removeAll(consumed);
            for(SlotWidget widget : toCheck) {
                if(EmiIngredient.areEqual(widget.getStack(), ing)) {
                    x = widget.getBounds().x();
                    y = widget.getBounds().y();
                    consumed.add(widget);
                    break;
                }
            }
        }
        return original.call(instance, recipeIngredientRole, x, y);
    }
}
