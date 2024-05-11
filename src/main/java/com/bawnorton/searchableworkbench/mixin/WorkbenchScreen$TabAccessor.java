package com.bawnorton.searchableworkbench.mixin;

import com.mrcrayfish.guns.crafting.WorkbenchRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(targets = "com.mrcrayfish.guns.client.screen.WorkbenchScreen$Tab", remap = false)
public interface WorkbenchScreen$TabAccessor {
    @Accessor
    List<WorkbenchRecipe> getItems();

    @Accessor @Mutable
    void setItems(List<WorkbenchRecipe> items);
}
