package com.bawnorton.tcgadditions.mixin.collectorsalbum;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.tnt.collectorsalbum.common.resource.AlbumBonusManager;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonus;
import java.util.Collections;
import java.util.List;

@Mixin(AlbumBonusManager.class)
public abstract class AlbumBonusManagerMixin {
    @Mutable
    @Shadow @Final private List<AlbumBonus> bonusList;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void useSynchronizedList(CallbackInfo ci) {
        bonusList = Collections.synchronizedList(bonusList);
    }
}
