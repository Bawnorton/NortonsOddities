package com.bawnorton.integrationfixes.compat.itemobliterator;

import elocindev.item_obliterator.forge.utils.Utils;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import java.util.ArrayList;
import java.util.List;

public class ItemObliteratorCompat {
    public static MerchantOffers getFiltered(MerchantOffers original) {
        MerchantOffers filtered = new MerchantOffers(Util.make(new CompoundTag(), tag -> tag.put("Recipes", new ListTag())));
        for (MerchantOffer offer : original) {
            if (!Utils.isDisabled(offer.assemble())) {
                filtered.add(offer);
            }
        }
        return filtered;
    }

    public static List<Integer> getDisabledIndices(MerchantOffers offers) {
        List<Integer> disabledIndices = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            MerchantOffer offer = offers.get(i);
            if (Utils.isDisabled(offer.assemble())) {
                disabledIndices.add(i);
            }
        }
        return disabledIndices;
    }
}
