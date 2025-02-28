package com.bawnorton.tcgadditions.mixin;

import com.bawnorton.tcgadditions.networking.C2S_InsertCards;
import com.bawnorton.tcgadditions.networking.S2C_OpenAlbumScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.network.NetworkManager;
import team.tnt.collectorsalbum.platform.network.PacketDirection;

@Mixin(NetworkManager.class)
public abstract class NetworkManagerMixin {
    @Inject(
            method = "init",
            at = @At("TAIL"),
            remap = false
    )
    private static void registerAdditionalPackets(CallbackInfo ci) {
        CollectorsAlbum.NETWORK_MANAGER.registerPacket(PacketDirection.SERVER_TO_CLIENT, S2C_OpenAlbumScreen.IDENTIFIER, S2C_OpenAlbumScreen.class, S2C_OpenAlbumScreen::read);
        CollectorsAlbum.NETWORK_MANAGER.registerPacket(PacketDirection.CLIENT_TO_SERVER, C2S_InsertCards.IDENTIFIER, C2S_InsertCards.class, b -> new C2S_InsertCards());
    }
}
