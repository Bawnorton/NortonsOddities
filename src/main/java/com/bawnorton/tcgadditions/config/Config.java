package com.bawnorton.tcgadditions.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.ConfigValue<Boolean> becomePlane;

    static {
        BUILDER.push("tcgadditions");

        becomePlane = BUILDER.define("become_plane", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static boolean isBecomePlane() {
        return !SPEC.isLoaded() ? becomePlane.getDefault() : becomePlane.get();
    }

    public static void setBecomePlane(boolean becomePlane) {
        if (!SPEC.isLoaded()) {
            return;
        }
        Config.becomePlane.set(becomePlane);
        SPEC.save();
    }
}
