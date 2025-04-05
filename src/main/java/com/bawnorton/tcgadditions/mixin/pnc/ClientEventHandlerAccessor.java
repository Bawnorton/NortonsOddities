package com.bawnorton.tcgadditions.mixin.pnc;

import me.desht.pneumaticcraft.client.event.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientEventHandler.class)
public interface ClientEventHandlerAccessor {
    @Accessor("currentScreenRoll")
    static float getCurrentScreenRoll() {
        throw new AssertionError("Mixin failed to apply");
    }

    @Accessor("currentScreenRoll")
    static void setCurrentScreenRoll(float currentScreenRoll) {
        throw new AssertionError("Mixin failed to apply");
    }
}
