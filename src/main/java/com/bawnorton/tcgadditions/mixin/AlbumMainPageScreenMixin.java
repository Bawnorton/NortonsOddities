package com.bawnorton.tcgadditions.mixin;

import com.bawnorton.tcgadditions.extend.BookmarkWidgetExtension;
import com.bawnorton.tcgadditions.networking.C2S_InsertCards;
import com.bawnorton.tcgadditions.networking.Networking;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.tnt.collectorsalbum.client.screen.AlbumMainPageScreen;
import team.tnt.collectorsalbum.client.screen.BookmarkWidget;
import team.tnt.collectorsalbum.common.AlbumCategory;
import java.util.List;

@Mixin(AlbumMainPageScreen.class)
public abstract class AlbumMainPageScreenMixin {
    @Inject(
            method = "getBookmarks",
            at = @At(
                    value = "INVOKE",
                    target = "Lteam/tnt/collectorsalbum/client/screen/AlbumNavigationHelper;listCategoriesForBookmarks(I)Ljava/util/List;"
            ),
            remap = false
    )
    private static void addInsertBookmark(int guiWidth, int guiHeight, int albumWidth, int albumHeight, int bookImageHeight, CallbackInfoReturnable<List<BookmarkWidget>> cir, @Local(name = "left") int left, @Local(name = "top") int top, @Local(name = "bookmarks") List<BookmarkWidget> bookmarks) {
        int bottom = top + 140;
        BookmarkWidget insert = new BookmarkWidget(left - 32, bottom, 32, 18, true, Items.ARROW.getDefaultInstance(), () -> false);
        insert.setTooltip(Tooltip.create(Networking.isOnServer() ? Component.translatable("tcgadditions.insert") : Component.translatable("tcgadditions.insert.disabled")));
        insert.setTooltipDelay(100);
        insert.setAction(() -> {
            if (Networking.isOnServer()) {
                Networking.sendServerMessage(new C2S_InsertCards());
            } else {
                LocalPlayer player = Minecraft.getInstance().player;
                player.sendSystemMessage(Component.translatable("tcgadditions.not_server"));
            }
        });
        bookmarks.add(insert);
    }

    @ModifyArg(
            method = "getBookmarks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    ordinal = 2
            ),
            remap = false
    )
    private static Object captureCategoryBookmark(Object obj, @Local(name = "category") AlbumCategory category) {
        if(!Networking.isOnServer()) return obj;

        BookmarkWidget bookmark = (BookmarkWidget) obj;
        ((BookmarkWidgetExtension) bookmark).tcgadditions$setCategory(category.identifier());
        return bookmark;
    }
}
