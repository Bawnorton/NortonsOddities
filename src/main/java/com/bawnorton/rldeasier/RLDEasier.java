package com.bawnorton.rldeasier;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = RLDEasier.MODID, version = RLDEasier.VERSION, name = RLDEasier.NAME, dependencies = "required-after:fermiumbooter")
public class RLDEasier {

    public static final String MODID = "rldeasier";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "RLDregoraEasier";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("RLDregoraEasier Loaded");
    }
}