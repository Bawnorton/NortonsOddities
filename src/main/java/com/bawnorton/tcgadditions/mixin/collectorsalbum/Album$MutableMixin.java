package com.bawnorton.tcgadditions.mixin.collectorsalbum;

import com.bawnorton.tcgadditions.extend.Album$MutableExtension;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import java.util.Map;

@Mixin(Album.Mutable.class)
public abstract class Album$MutableMixin implements Album$MutableExtension {
    @Shadow @Final private Map<ResourceLocation, NonNullList<ItemStack>> inventories;

    @Override
    public ItemStack tcgadditions$get(ResourceLocation category, int index) {
        NonNullList<ItemStack> inventory = inventories.get(category);
        if (inventory == null) {
            AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
            AlbumCategory albumCategory = manager.findById(category).orElseThrow(() -> new IllegalArgumentException(String.format("Attempting to get item at index %s from unknown category %s", index, category)));
            inventory = NonNullList.withSize(albumCategory.getCardNumbers().length, ItemStack.EMPTY);
            this.inventories.put(category, inventory);
        }
        return inventory.get(index);
    }
}
