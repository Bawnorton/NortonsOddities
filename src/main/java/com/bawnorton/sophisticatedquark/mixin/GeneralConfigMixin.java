package com.bawnorton.sophisticatedquark.mixin;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.quark.base.handler.GeneralConfig;
import java.util.List;

@Debug(export = true)
@Mixin(GeneralConfig.class)
public abstract class GeneralConfigMixin {
    @Shadow @Final private static List<String> STATIC_ALLOWED_SCREENS;

    static {
        STATIC_ALLOWED_SCREENS.addAll(List.of(
            "net.p3pp3rf1y.sophisticatedstorage.client.gui.StorageScreen",
            "net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen",
            "net.p3pp3rf1y.sophisticatedstorage.client.gui.LimitedBarrelScreen"
        ));
    }
}
