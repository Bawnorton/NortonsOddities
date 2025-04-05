package com.bawnorton.tcgadditions.mixin.pnc;

import com.bawnorton.tcgadditions.TCGAdditions;
import javax.annotation.Nullable;
import me.desht.pneumaticcraft.common.item.PneumaticArmorItem;
import me.desht.pneumaticcraft.common.pneumatic_armor.JetBootsStateTracker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract void setXRot(float pXRot);

    @Shadow public abstract float getXRot();

    @Shadow public float xRotO;

    @Shadow @Nullable private Entity vehicle;

    @Shadow public abstract void setYRot(float pYRot);

    @Shadow public abstract float getYRot();

    @Inject(
            method = "turn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void unlockCameraRoll(double pYRot, double pXRot, CallbackInfo ci) {
        if((Object) this instanceof Player player) {
            if (!PneumaticArmorItem.isPneumaticArmorPiece(player, EquipmentSlot.FEET) || player.onGround()) return;

            JetBootsStateTracker.JetBootsState jbState = JetBootsStateTracker.getClientTracker().getJetBootsState(player);
            if (!jbState.isActive() || jbState.isBuilderMode()) {
                if(Math.abs(this.getXRot()) > 90) {
                    this.setXRot(Mth.clamp(this.getXRot(), -90, 90));
                }
                return;
            }

            float currentScreenRoll = ClientEventHandlerAccessor.getCurrentScreenRoll();
            float scaledYRot = (float) (pYRot * 0.075F);
            currentScreenRoll += scaledYRot;
            currentScreenRoll = Mth.wrapDegrees(currentScreenRoll);
            ClientEventHandlerAccessor.setCurrentScreenRoll(currentScreenRoll);

            this.setYRot(this.getYRot() + Mth.clamp(currentScreenRoll, -45, 45) / 120);

            float f = (float)pXRot * 0.15F;
            this.setXRot(this.getXRot() + f);
            this.setXRot(Mth.wrapDegrees(this.getXRot()));
            this.xRotO += f;
            this.xRotO = Mth.wrapDegrees(this.xRotO);
            if (this.vehicle != null) {
                this.vehicle.onPassengerTurned(player);
            }
            ci.cancel();
        }
    }
}
