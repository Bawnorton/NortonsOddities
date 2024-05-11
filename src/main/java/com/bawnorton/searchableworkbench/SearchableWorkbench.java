package com.bawnorton.searchableworkbench;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SearchableWorkbench.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SearchableWorkbench {
    public static final String MODID = "searchableworkbench";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SearchableWorkbench() {}
}
