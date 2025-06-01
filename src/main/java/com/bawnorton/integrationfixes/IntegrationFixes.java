package com.bawnorton.integrationfixes;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(IntegrationFixes.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class IntegrationFixes {
    public static final String MOD_ID = "integrationfixes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IntegrationFixes() {}

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
