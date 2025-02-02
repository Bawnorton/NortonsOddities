package com.bawnorton.taf.platform;

import com.bawnorton.taf.ModId;

//? if forge {
import net.minecraftforge.fml.common.Mod;

@Mod(ModId.MOD_ID)
public final class ModIdWrapper {
    public ModIdWrapper() {
        ModId.init();
    }
}
//?}
