package com.bawnorton.sporeadditions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class FreezeData extends SavedData {
    public static final String ID = "sporeadditions:freeze_data";
    private boolean isFrozen;

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("isFrozen", isFrozen);
        return tag;
    }

    public static FreezeData load(CompoundTag tag) {
        FreezeData data = new FreezeData();
        if (tag.contains("isFrozen")) {
            data.isFrozen = tag.getBoolean("isFrozen");
        } else {
            data.isFrozen = false;
        }
        return data;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
        setDirty();
    }
}
