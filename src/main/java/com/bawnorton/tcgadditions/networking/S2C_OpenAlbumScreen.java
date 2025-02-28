package com.bawnorton.tcgadditions.networking;

import com.bawnorton.tcgadditions.TCGAdditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.init.RegistryTags;
import team.tnt.collectorsalbum.platform.Platform;
import team.tnt.collectorsalbum.platform.network.NetworkMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S2C_OpenAlbumScreen implements NetworkMessage {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation("tcgadditions", "msg_open_album_screen");
    private final Map<ResourceLocation, List<Integer>> slotsToHighlight;

    public S2C_OpenAlbumScreen(Map<ResourceLocation, List<Integer>> slotsToHighlight) {
        this.slotsToHighlight = slotsToHighlight;
    }

    @Override
    public ResourceLocation getPacketId() {
        return IDENTIFIER;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(slotsToHighlight.size());
        for (Map.Entry<ResourceLocation, List<Integer>> entry : slotsToHighlight.entrySet()) {
            friendlyByteBuf.writeResourceLocation(entry.getKey());
            friendlyByteBuf.writeInt(entry.getValue().size());
            for (int slot : entry.getValue()) {
                friendlyByteBuf.writeInt(slot);
            }
        }
    }

    public static S2C_OpenAlbumScreen read(FriendlyByteBuf friendlyByteBuf) {
        int size = friendlyByteBuf.readInt();
        Map<ResourceLocation, List<Integer>> slotsToHighlight = new HashMap<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation category = friendlyByteBuf.readResourceLocation();
            int slotSize = friendlyByteBuf.readInt();
            List<Integer> slots = new ArrayList<>();
            for (int j = 0; j < slotSize; j++) {
                slots.add(friendlyByteBuf.readInt());
            }
            slotsToHighlight.put(category, slots);
        }
        return new S2C_OpenAlbumScreen(slotsToHighlight);
    }

    @Override
    public void handle(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        if (!itemStack.is(RegistryTags.Items.ALBUM)) return;

        Album album = Album.get(itemStack);
        if (album == null) return;

        Platform.INSTANCE.openAlbumUi(itemStack);
        TCGAdditions.SLOTS_TO_HIGHLIGHT.set(slotsToHighlight);

        Screen current = Minecraft.getInstance().screen;
        if(current == null) return;

        current.resize(Minecraft.getInstance(), current.width, current.height);
    }
}
