package com.bawnorton.taf.platform;

import com.bawnorton.taf.TetraApotheosisFix;

//? if forge {
import net.minecraftforge.fml.common.Mod;

@Mod(TetraApotheosisFix.MOD_ID)
public final class TetraApotheosisFixWrapper {
    public TetraApotheosisFixWrapper() {
        TetraApotheosisFix.init();
    }
}
//?}
