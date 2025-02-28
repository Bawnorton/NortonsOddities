package com.bawnorton.tcgadditions;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(TCGAdditions.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCGAdditions {
    public static final String MODID = "tcgadditions";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ThreadLocal<Map<ResourceLocation, List<Integer>>> SLOTS_TO_HIGHLIGHT = ThreadLocal.withInitial(HashMap::new);

    public TCGAdditions() {}
}
