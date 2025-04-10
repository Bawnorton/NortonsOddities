package com.bawnorton.tcgadditions.mixin.pnc;

import com.bawnorton.tcgadditions.TCGAdditions;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.desht.pneumaticcraft.client.pneumatic_armor.upgrade_handler.JetBootsClientHandler;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Collection;

@Mixin(JetBootsClientHandler.class)
public abstract class JetBootsClientHandlerMixin {
    @ModifyReturnValue(
            method = "getSubKeybinds",
            at = @At("RETURN"),
            remap = false
    )
    private Collection<ResourceLocation> addBecomePlane(Collection<ResourceLocation> original) {
        return ImmutableList.<ResourceLocation>builder().addAll(original).add(TCGAdditions.id("become_plane")).build();
    }
}
