package com.bawnorton.tcgadditions.mixin;

import com.bawnorton.tcgadditions.TCGAdditions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.tnt.collectorsalbum.client.screen.AlbumCategoryScreen;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryUiTemplate;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(AlbumCategoryScreen.class)
public abstract class AlbumCategoryScreenMixin extends AbstractContainerScreen<AlbumCategoryMenu> {
    @Shadow(remap = false) @Final private AlbumCategory category;

    @Unique
    private final Map<ResourceLocation, Map<Integer, Long>> tcgadditions$DELAY_MAP = new HashMap<>();

    public AlbumCategoryScreenMixin(AlbumCategoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @WrapOperation(
            method = "renderBg",
            at = @At(
                    value = "INVOKE",
                    target = "Lteam/tnt/collectorsalbum/client/screen/AlbumCategoryScreen;blitTextureTemplate(Lnet/minecraft/client/gui/GuiGraphics;IILteam/tnt/collectorsalbum/common/AlbumCategoryUiTemplate$TextureTemplate;)V",
                    ordinal = 1,
                    remap = false
            )
    )
    private void highlightSlots(GuiGraphics guiGraphics, int x, int y, AlbumCategoryUiTemplate.TextureTemplate template, Operation<Void> original, @Local(name = "slot") int slot, @Local(name = "cardSlot") Slot cardSlot) {
        List<Integer> toHighlight = TCGAdditions.SLOTS_TO_HIGHLIGHT.get().getOrDefault(category.identifier(), List.of());
        boolean changed = false;
        if (toHighlight.contains(slot)) {
            if(cardSlot.equals(hoveredSlot)) {
                toHighlight.remove((Integer) slot);
            } else {
                RenderSystem.setShaderColor(0.0F, 1.0F, 1.0F, 1.0F);
                Map<Integer, Long> categoryDelayMap = tcgadditions$DELAY_MAP.computeIfAbsent(category.identifier(), k -> new HashMap<>());
                categoryDelayMap.computeIfAbsent(slot, k -> System.currentTimeMillis());
                categoryDelayMap.computeIfPresent(slot, (k, v) -> {
                    if (System.currentTimeMillis() - v > 700) {
                        toHighlight.remove(k);
                        return null;
                    }
                    return v;
                });
                changed = true;
            }
        }
        original.call(guiGraphics, x, y, template);
        if (changed) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        if(toHighlight.isEmpty()) {
            TCGAdditions.SLOTS_TO_HIGHLIGHT.get().remove(category.identifier());
        }
    }

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void renderDarkBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.renderBackground(graphics);
    }
}
