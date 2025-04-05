package com.bawnorton.tcgadditions.networking;

import com.bawnorton.tcgadditions.TCGAdditions;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Networking {
    public static final EventNetworkChannel EXISTENCE_CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(TCGAdditions.MOD_ID, "exists"))
            .networkProtocolVersion(() -> "yes")
            .clientAcceptedVersions(Predicates.alwaysTrue())
            .serverAcceptedVersions(Predicates.alwaysTrue())
            .eventNetworkChannel();

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(TCGAdditions.MOD_ID, "main"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(Predicates.alwaysTrue())
            .serverAcceptedVersions(Predicates.alwaysTrue())
            .simpleChannel();

    public static boolean isOnServer() {
        return EXISTENCE_CHANNEL.isRemotePresent(Minecraft.getInstance()
                .getConnection()
                .getConnection());
    }

    public static boolean isOnClient(ServerPlayer player) {
        return EXISTENCE_CHANNEL.isRemotePresent(player.connection.connection);
    }

    public static void init() {
        CHANNEL.messageBuilder(S2C_OpenAlbumScreen.class, 0)
                .encoder(S2C_OpenAlbumScreen::write)
                .decoder(S2C_OpenAlbumScreen::read)
                .consumerMainThread((t, contextSupplier) -> {
                    t.handle(getPlayer(contextSupplier.get()));
                    contextSupplier.get().setPacketHandled(true);
                })
                .add();
        CHANNEL.messageBuilder(C2S_InsertCards.class, 1)
                .encoder((packet, buf) -> {})
                .decoder(buf -> new C2S_InsertCards())
                .consumerMainThread((t, contextSupplier) -> {
                    t.handle(getPlayer(contextSupplier.get()));
                    contextSupplier.get().setPacketHandled(true);
                })
                .add();
    }

    public static void sendServerMessage(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendClientMessage(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    private static Player getPlayer(NetworkEvent.Context ctx) {
        return ctx.getDirection()
                .getReceptionSide()
                .isServer() ? ctx.getSender() : getLocalPlayer();
    }

    @OnlyIn(Dist.CLIENT)
    private static Player getLocalPlayer() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player;
    }
}
