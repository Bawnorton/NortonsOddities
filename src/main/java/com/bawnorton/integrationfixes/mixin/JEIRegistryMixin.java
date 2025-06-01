package com.bawnorton.integrationfixes.mixin;

import com.bawnorton.integrationfixes.compat.eidolon.WorktableRecipeTransferHandler;
import elucent.eidolon.gui.jei.JEIRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(JEIRegistry.class)
public abstract class JEIRegistryMixin implements IModPlugin {
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new WorktableRecipeTransferHandler());
    }
}
