package com.bawnorton.tcgadditions.mixin.pnc;

import com.bawnorton.tcgadditions.TCGAdditions;
import me.desht.pneumaticcraft.client.event.ClientEventHandler;
import me.desht.pneumaticcraft.common.item.PneumaticArmorItem;
import me.desht.pneumaticcraft.common.pneumatic_armor.JetBootsStateTracker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientEventHandler.class)
public abstract class ClientEventHandlerMixin {
    @Shadow private static float currentScreenRoll;

    @Inject(
            method = "screenTilt",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void unlockCameraRoll(ViewportEvent.ComputeCameraAngles event, CallbackInfo ci) {
        if (!(event.getCamera().getEntity() instanceof Player player)) return;
        if (!PneumaticArmorItem.isPneumaticArmorPiece(player, EquipmentSlot.FEET) || player.onGround()) return;

        JetBootsStateTracker.JetBootsState jbState = JetBootsStateTracker.getClientTracker().getJetBootsState(player);
        if (!jbState.isActive() || jbState.isBuilderMode()) return;
        if(!TCGAdditions.isBecomePlane()) return;

        event.setRoll(currentScreenRoll);

        ci.cancel();
    }
}
