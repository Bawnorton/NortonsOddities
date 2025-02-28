package com.bawnorton.tcgadditions;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TCGAdditions.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCGAdditions {
    public static final String MODID = "tcgadditions";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TCGAdditions() {}
}
