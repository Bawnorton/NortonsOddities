package com.bawnorton.tcgadditions.mixin.pnc;

import com.bawnorton.tcgadditions.TCGAdditions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.desht.pneumaticcraft.api.client.pneumatic_helmet.IClientArmorRegistry;
import me.desht.pneumaticcraft.api.client.pneumatic_helmet.IGuiScreen;
import me.desht.pneumaticcraft.api.pneumatic_armor.IArmorUpgradeHandler;
import me.desht.pneumaticcraft.client.gui.pneumatic_armor.options.JetBootsOptions;
import me.desht.pneumaticcraft.client.gui.widget.WidgetCheckBox;
import me.desht.pneumaticcraft.client.render.pneumatic_armor.HUDHandler;
import me.desht.pneumaticcraft.client.util.PointXY;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JetBootsOptions.class)
public abstract class JetBootsOptionsMixin {
    @Unique
    private static final ResourceLocation PLANE_BUTTON = TCGAdditions.id("become_plane");

    @Inject(
            method = "populateGui",
            at = @At("TAIL"),
            remap = false
    )
    private void addPlaneButton(IGuiScreen gui, CallbackInfo ci, @Local IClientArmorRegistry registry, @Local(name = "ownerID") ResourceLocation ownerID) {
        var checkbox = registry.makeKeybindingCheckBox(PLANE_BUTTON, 5, 120, -1, b -> {
            TCGAdditions.setBecomePlane(b.isChecked());
            HUDHandler.getInstance().addFeatureToggleMessage(IArmorUpgradeHandler.getStringKey(ownerID), IArmorUpgradeHandler.getStringKey(b.getUpgradeId()), b.isChecked());
        }).withOwnerUpgradeID(ownerID).asWidget();
        ((WidgetCheckBox) checkbox).setChecked(TCGAdditions.isBecomePlane());
        gui.addWidget(checkbox);
    }

    @ModifyReturnValue(
            method = "getSliderPos",
            at = @At("RETURN"),
            remap = false
    )
    private PointXY shiftDown10(PointXY original) {
        return new PointXY(original.x(), original.y() + 10);
    }
}
