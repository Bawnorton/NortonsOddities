package com.bawnorton.eidolonworkbenchjeicompat;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(EWJEICompat.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EWJEICompat {
    public static final String MOD_ID = "eidolonworkbenchjeicompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EWJEICompat() {}

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
