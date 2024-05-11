package com.bawnorton.sophisticatedquark;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SophisticatedQuark.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SophisticatedQuark {
    public static final String MODID = "sophisticatedquark";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SophisticatedQuark() {}
}
