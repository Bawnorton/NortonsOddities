package com.bawnorton.tcgadditions;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
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
    public static final ThreadLocal<Float> INPUT_Y_ROT = ThreadLocal.withInitial(() -> 0f);

    public static Class<?> CARD_SLOT_CLASS;

    static {
        try {
            CARD_SLOT_CLASS = Class.forName("team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu$CardSlot");
        } catch (ClassNotFoundException e) {
            CARD_SLOT_CLASS = null;
            TCGAdditions.LOGGER.error("Failed to find AlbumCategoryMenu$CardSlot class", e);
        }
    }

    public TCGAdditions() {}
}
