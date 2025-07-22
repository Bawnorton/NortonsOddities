package com.bawnorton.sporeadditions.jei;

import com.Harbinger.Spore.Compat.SurgeryCraftingCategory;
import com.Harbinger.Spore.Core.SMenu;
import com.Harbinger.Spore.Recipes.SurgeryRecipe;
import com.Harbinger.Spore.Screens.SurgeryMenu;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import java.util.List;
import java.util.Optional;

public class SurgeryRecipeTransferInfo implements IRecipeTransferInfo<SurgeryMenu, SurgeryRecipe> {
    @Override
    public Class<? extends SurgeryMenu> getContainerClass() {
        return SurgeryMenu.class;
    }

    @Override
    public Optional<MenuType<SurgeryMenu>> getMenuType() {
        return Optional.of(SMenu.SURGERY_MENU.get());
    }

    @Override
    public RecipeType<SurgeryRecipe> getRecipeType() {
        return SurgeryCraftingCategory.SURGERY_TYPE;
    }

    @Override
    public boolean canHandle(SurgeryMenu container, SurgeryRecipe recipe) {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(SurgeryMenu container, SurgeryRecipe recipe) {
        return container.slots.stream().filter(slot -> slot.index >= 36).toList();
    }

    @Override
    public List<Slot> getInventorySlots(SurgeryMenu container, SurgeryRecipe recipe) {
        return container.slots.stream().filter(slot -> slot.container instanceof Inventory).toList();
    }
}
