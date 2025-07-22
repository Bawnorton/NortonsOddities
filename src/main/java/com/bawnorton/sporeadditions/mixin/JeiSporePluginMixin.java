package com.bawnorton.sporeadditions.mixin;

import com.Harbinger.Spore.Compat.JeiSporePlugin;
import com.Harbinger.Spore.Compat.SurgeryCraftingCategory;
import com.bawnorton.sporeadditions.jei.SurgeryRecipeTransferHandler;
import com.bawnorton.sporeadditions.jei.SurgeryRecipeTransferInfo;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.common.Internal;
import mezz.jei.common.network.IConnectionToServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(JeiSporePlugin.class)
public abstract class JeiSporePluginMixin implements IModPlugin {
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IConnectionToServer serverConnection = Internal.getServerConnection();
        IStackHelper stackHelper = registration.getJeiHelpers().getStackHelper();
        IRecipeTransferHandlerHelper handlerHelper = registration.getTransferHelper();
        registration.addRecipeTransferHandler(new SurgeryRecipeTransferHandler(serverConnection, stackHelper, handlerHelper, new SurgeryRecipeTransferInfo()), SurgeryCraftingCategory.SURGERY_TYPE);
    }
}