package com.bawnorton.tcgadditions.mixin;

import com.bawnorton.tcgadditions.networking.Networking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.tnt.collectorsalbum.network.NetworkManager;

@Mixin(NetworkManager.class)
public abstract class NetworkManagerMixin {
    @Inject(
            method = "init",
            at = @At("TAIL"),
            remap = false
    )
    private static void initNetworking(CallbackInfo ci) {
        Networking.init();
    }
}
