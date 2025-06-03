package com.bawnorton.integrationfixes.mixin.accessor;

import elucent.eidolon.gui.WorktableContainer;
import net.minecraft.world.inventory.CraftingContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorktableContainer.class)
public interface WorktableContainerAccessor {
    @Accessor(value = "core", remap = false)
    CraftingContainer integrationfixes$getCore();

    @Accessor(value = "extras", remap = false)
    CraftingContainer integrationfixes$getExtras();
}
