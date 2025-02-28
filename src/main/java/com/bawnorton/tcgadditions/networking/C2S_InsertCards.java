package com.bawnorton.tcgadditions.networking;

import com.bawnorton.tcgadditions.TCGAdditions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.init.RegistryTags;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.network.NetworkMessage;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class C2S_InsertCards implements NetworkMessage {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation("tcgadditions", "msg_insert_cards");

    @Override
    public ResourceLocation getPacketId() {
        return IDENTIFIER;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public void handle(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        if (!itemStack.is(RegistryTags.Items.ALBUM)) return;

        Album album = Album.get(itemStack);
        if (album == null) return;

        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        AlbumCardManager cardManager = AlbumCardManager.getInstance();
        List<ResourceLocation> categories = manager.listCategories().stream().map(AlbumCategory::identifier).toList();
        Map<AlbumCard, ItemStack> inventoryCards = player.getInventory().items
                .stream()
                .filter(stack -> cardManager.isCard(stack.getItem()))
                .collect(HashMap::new, (map, stack) -> map.put(cardManager.getCardInfo(stack.getItem()).orElseThrow(), stack), Map::putAll);
        if(inventoryCards.isEmpty()) return;

        Map<ResourceLocation, List<Integer>> slotsToHighlight = new HashMap<>();

        Map<Integer, Predicate<Integer>> emptySlotMap = player.containerMenu.slots
                .stream()
                .filter(slot -> TCGAdditions.CARD_SLOT_CLASS.isInstance(slot))
                .collect(HashMap::new, (map, slot) -> map.put(slot.index, index -> !slot.hasItem()), Map::putAll);

        try {
            Album.Mutable mutable = new Album.Mutable(album);
            for (ResourceLocation category : categories) {
                // replace with better cards
                Collection<AlbumCard> cards = album.getCardsForCategory(category);
                cards.removeIf(card -> emptySlotMap.get(card.cardNumber() - 1).test(card.cardNumber() - 1));
                for (AlbumCard albumCard : cards) {
                    List<AlbumCard> toRemove = new ArrayList<>();
                    for (Map.Entry<AlbumCard, ItemStack> entry : inventoryCards.entrySet()) {
                        AlbumCard inventoryCard = entry.getKey();
                        if (!inventoryCard.identifier().equals(albumCard.identifier())) continue;

                        if (inventoryCard.compareTo(albumCard) > 0) {
                            mutable.set(category, inventoryCard.cardNumber() - 1, inventoryCard.asItem());
                            entry.getValue().shrink(1);
                            slotsToHighlight.computeIfAbsent(category, k -> new ArrayList<>()).add(inventoryCard.cardNumber() - 1);
                        }
                        toRemove.add(inventoryCard);
                    }
                    toRemove.forEach(inventoryCards::remove);
                }

                // add new cards
                for (Map.Entry<AlbumCard, ItemStack> entry : inventoryCards.entrySet()) {
                    AlbumCard inventoryCard = entry.getKey();
                    if (inventoryCard.category().equals(category)) {
                        mutable.set(category, inventoryCard.cardNumber() - 1, inventoryCard.asItem());
                        entry.getValue().shrink(1);
                        slotsToHighlight.computeIfAbsent(category, k -> new ArrayList<>()).add(inventoryCard.cardNumber() - 1);
                    }
                }
            }
            Album updated = mutable.toImmutable();
            Album.set(itemStack, updated);
            player.getInventory().setChanged();

            PlatformNetworkManager.NETWORK.sendClientMessage((ServerPlayer) player, new S2C_OpenAlbumScreen(slotsToHighlight));
        } catch (RuntimeException e) {
            TCGAdditions.LOGGER.error("Failed to insert card into album", e);
        }
    }
}
