package com.bawnorton.tempadwithshaders;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(TempadWithShaders.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TempadWithShaders {
    public static final String MODID = "tempadwithshaders";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TempadWithShaders() {}
}
