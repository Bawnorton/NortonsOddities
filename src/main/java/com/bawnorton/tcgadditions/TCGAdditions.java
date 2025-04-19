package com.bawnorton.tcgadditions;

import com.bawnorton.tcgadditions.config.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(TCGAdditions.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCGAdditions {
    public static final String MOD_ID = "tcgadditions";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ThreadLocal<Map<ResourceLocation, List<Integer>>> SLOTS_TO_HIGHLIGHT = ThreadLocal.withInitial(HashMap::new);

    public static Class<?> CARD_SLOT_CLASS;

    static {
        try {
            CARD_SLOT_CLASS = Class.forName("team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu$CardSlot");
        } catch (ClassNotFoundException e) {
            CARD_SLOT_CLASS = null;
            TCGAdditions.LOGGER.error("Failed to find AlbumCategoryMenu$CardSlot class", e);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC, "tcgadditions.toml");
    }

    public TCGAdditions() {}

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static boolean isBecomePlane() {
        return Config.isBecomePlane();
    }

    public static void setBecomePlane(boolean becomePlane) {
        Config.setBecomePlane(becomePlane);
    }
}
