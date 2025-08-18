package com.bawnorton.eidolonworkbenchjeicompat;

import com.bawnorton.eidolonworkbenchjeicompat.mixin.accessor.WorktableContainerAccessor;
import elucent.eidolon.gui.WorktableContainer;
import elucent.eidolon.gui.jei.JEIRegistry;
import elucent.eidolon.recipe.WorktableRecipe;
import elucent.eidolon.registries.Registry;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorktableRecipeTransferHandler implements IRecipeTransferInfo<WorktableContainer, WorktableRecipe> {
    @Override
    public @NotNull Class<? extends WorktableContainer> getContainerClass() {
        return WorktableContainer.class;
    }

    @Override
    public @NotNull Optional<MenuType<WorktableContainer>> getMenuType() {
        return Optional.of(Registry.WORKTABLE_CONTAINER.get());
    }

    @Override
    public @NotNull RecipeType<WorktableRecipe> getRecipeType() {
        return JEIRegistry.WORKTABLE_CATEGORY;
    }

    @Override
    public boolean canHandle(@NotNull WorktableContainer container, @NotNull WorktableRecipe recipe) {
        return true;
    }

    @Override
    public @NotNull List<Slot> getRecipeSlots(@NotNull WorktableContainer container, @NotNull WorktableRecipe recipe) {
        WorktableContainerAccessor accessor = (WorktableContainerAccessor) container;
        CraftingContainer core = accessor.eidolonworkbenchjeicompat$getCore();
        CraftingContainer extras = accessor.eidolonworkbenchjeicompat$getExtras();
        List<Slot> coreSlots = container.slots.stream()
                .filter(slot -> slot.container == core)
                .collect(ArrayList::new, List::add, List::addAll);
        List<Slot> extraSlots = container.slots.stream()
                .filter(slot -> slot.container == extras)
                .toList();
        coreSlots.addAll(extraSlots);
        return coreSlots;
    }

    @Override
    public @NotNull List<Slot> getInventorySlots(WorktableContainer container, @NotNull WorktableRecipe recipe) {
        return container.slots.stream().filter(slot -> slot.container instanceof Inventory).toList();
    }
}
