package com.bawnorton.tcgadditions.networking;

import com.bawnorton.tcgadditions.TCGAdditions;
import com.bawnorton.tcgadditions.extend.Album$MutableExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.init.RegistryTags;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C2S_InsertCards {
    public void handle(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        if (!itemStack.is(RegistryTags.Items.ALBUM))
            return;

        Album album = Album.get(itemStack);
        if (album == null)
            return;

        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        AlbumCardManager cardManager = AlbumCardManager.getInstance();
        List<ResourceLocation> categories = manager.listCategories()
                .stream()
                .map(AlbumCategory::identifier)
                .toList();
        Map<AlbumCard, ItemStack> inventoryCards = player.getInventory().items
                .stream()
                .filter(stack -> cardManager.isCard(stack.getItem()))
                .collect(HashMap::new, (map, stack) -> map.put(cardManager.getCardInfo(stack.getItem())
                        .orElseThrow(), stack), Map::putAll);
        if (inventoryCards.isEmpty())
            return;

        Map<ResourceLocation, List<Integer>> slotsToHighlight = new HashMap<>();

        Map<Integer, Boolean> emptySlotMap = player.containerMenu.slots
                .stream()
                .filter(slot -> TCGAdditions.CARD_SLOT_CLASS.isInstance(slot))
                .collect(HashMap::new, (map, slot) -> map.put(slot.index, !slot.hasItem()), Map::putAll);

        int insertCount = 0;
        try {
            Album.Mutable mutable = new Album.Mutable(album);
            Album$MutableExtension mutableExtension = (Album$MutableExtension) (Object) mutable;
            for (ResourceLocation category : categories) {
                Collection<AlbumCard> cards = album.getCardsForCategory(category);
                if (player.containerMenu instanceof AlbumCategoryMenu albumCategoryMenu && albumCategoryMenu.getCategory()
                        .identifier()
                        .equals(category)) {
                    cards.removeIf(card -> emptySlotMap.get(card.cardNumber() - 1));
                }

                for (Map.Entry<AlbumCard, ItemStack> entry : inventoryCards.entrySet()) {
                    AlbumCard inventoryCard = entry.getKey();
                    if (!inventoryCard.category().equals(category)) continue;

                    ItemStack stack = mutableExtension.tcgadditions$get(category, inventoryCard.cardNumber() - 1);
                    if (stack.isEmpty()) {
                        mutable.set(category, inventoryCard.cardNumber() - 1, inventoryCard.asItem());
                        entry.getValue().shrink(1);
                        slotsToHighlight.computeIfAbsent(category, k -> new ArrayList<>()).add(inventoryCard.cardNumber() - 1);
                        insertCount++;
                    } else {
                        AlbumCard existingCard = cardManager.getCardInfo(stack.getItem()).orElseThrow();
                        if (existingCard.compareTo(inventoryCard) < 0) {
                            mutable.set(category, inventoryCard.cardNumber() - 1, inventoryCard.asItem());
                            entry.getValue().shrink(1);
                            slotsToHighlight.computeIfAbsent(category, k -> new ArrayList<>()).add(inventoryCard.cardNumber() - 1);
                            insertCount++;
                            if (!player.addItem(stack)) {
                                player.drop(stack, true, false);
                            }
                        }
                    }
                }
            }
            Album updated = mutable.toImmutable();
            Album.set(itemStack, updated);
            player.getInventory().setChanged();

            if(insertCount == 0) {
                player.sendSystemMessage(Component.translatable("tcgadditions.inserted.cards.none"));
            } else if (insertCount == 1) {
                player.sendSystemMessage(Component.translatable("tcgadditions.inserted.cards.single"));
            } else {
                player.sendSystemMessage(Component.translatable("tcgadditions.inserted.cards.multiple", Component.literal(String.valueOf(insertCount)).withStyle(style -> style.withColor(ChatFormatting.AQUA))));
            }

            Networking.sendClientMessage((ServerPlayer) player, new S2C_OpenAlbumScreen(slotsToHighlight));
        } catch (RuntimeException e) {
            TCGAdditions.LOGGER.error("Failed to insert card into album", e);
        }
    }
}
