package com.bawnorton.tcgadditions.mixin;

import com.bawnorton.tcgadditions.TCGAdditions;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.tnt.collectorsalbum.client.screen.AlbumCategoryScreen;
import team.tnt.collectorsalbum.client.screen.AlbumMainPageScreen;
import team.tnt.collectorsalbum.client.screen.BookmarkWidget;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Mixin(AlbumMainPageScreen.class)
public abstract class AlbumMainPageScreenMixin {
    @Unique
    private static final ThreadLocal<Map<ResourceLocation, BookmarkWidget>> tcgadditions$CATEGORY_BOOKMARKS = ThreadLocal.withInitial(HashMap::new);

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
        insert.setTooltip(Tooltip.create(Component.translatable("tcgadditions.insert")));
        insert.setTooltipDelay(100);
        insert.setAction(() -> {
            Map<ResourceLocation, BookmarkWidget> categories = tcgadditions$CATEGORY_BOOKMARKS.get();
            if (categories.isEmpty()) return;

            CompletableFuture<Void> all = CompletableFuture.completedFuture(null);

            for (Map.Entry<ResourceLocation, BookmarkWidget> entry : categories.entrySet()) {
                BookmarkWidget bookmark = entry.getValue();
                ResourceLocation key = entry.getKey();
                all = all.thenComposeAsync(v -> {
                    CompletableFuture<Void> clickFuture = CompletableFuture.runAsync(() -> bookmark.onClick(0, 0), Minecraft.getInstance());

                    CompletableFuture<Boolean> waitingFuture = clickFuture.thenComposeAsync(v2 -> CompletableFuture.supplyAsync(() -> {
                        long start = System.currentTimeMillis();
                        long timeout = 500;
                        while (System.currentTimeMillis() - start < timeout) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                return false;
                            }
                            if (Minecraft.getInstance().screen instanceof AlbumCategoryScreen albumCategoryScreen) {
                                return albumCategoryScreen.getCategory().identifier().equals(key);
                            }
                        }
                        return false;
                    }), ForkJoinPool.commonPool());

                    return waitingFuture.thenComposeAsync(inCategory -> {
                        if (!inCategory) return CompletableFuture.completedFuture(null);

                        return CompletableFuture.runAsync(() -> {
                            if (!(Minecraft.getInstance().screen instanceof AlbumCategoryScreen albumCategoryScreen)) return;

                            AlbumCategoryMenu menu = albumCategoryScreen.getMenu();
                            menu.slots.forEach(slot -> {
                                ItemStack stack = slot.getItem();
                                AlbumCardManager cardManager = AlbumCardManager.getInstance();
                                if (!cardManager.isCard(stack.getItem())) return;

                                Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.QUICK_MOVE, Minecraft.getInstance().player);
                            });
                        }, Minecraft.getInstance());
                    }, Minecraft.getInstance());
                }, Minecraft.getInstance());

                all.thenRunAsync(() -> {
                    TCGAdditions.LOGGER.info("All Cards Inserted");
                }, Minecraft.getInstance()).exceptionally(e -> {
                    TCGAdditions.LOGGER.error("Error inserting cards", e);
                    return null;
                });
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
        BookmarkWidget bookmark = (BookmarkWidget) obj;
        tcgadditions$CATEGORY_BOOKMARKS.get().put(category.identifier(), bookmark);
        return bookmark;
    }
}
