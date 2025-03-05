package com.bawnorton.tcgadditions.mixin.collectorsalbum;

import com.bawnorton.tcgadditions.TCGAdditions;
import com.bawnorton.tcgadditions.extend.BookmarkWidgetExtension;
import com.bawnorton.tcgadditions.networking.Networking;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import team.tnt.collectorsalbum.client.screen.BookmarkWidget;

@Mixin(BookmarkWidget.class)
public abstract class BookmarkWidgetMixin implements BookmarkWidgetExtension {
    @Unique
    private ResourceLocation tcgadditions$category;

    @Override
    public void tcgadditions$setCategory(ResourceLocation category) {
        tcgadditions$category = category;
    }

    @WrapOperation(
            method = "renderWidget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"
            )
    )
    private void highlightBookmarks(GuiGraphics instance, ResourceLocation pAtlasLocation, int pX, int pY, int pBlitOffset, float pUOffset, float pVOffset, int pUWidth, int pVHeight, int pTextureWidth, int pTextureHeight, Operation<Void> original) {
        if(!Networking.isOnServer()) {
            original.call(instance, pAtlasLocation, pX, pY, pBlitOffset, pUOffset, pVOffset, pUWidth, pVHeight, pTextureWidth, pTextureHeight);
            return;
        }

        boolean changed = false;
        if(tcgadditions$category != null && TCGAdditions.SLOTS_TO_HIGHLIGHT.get().containsKey(tcgadditions$category)) {
            RenderSystem.setShaderColor(0.0F, 1.0F, 1.0F, 1.0F);
            changed = true;
        }
        original.call(instance, pAtlasLocation, pX, pY, pBlitOffset, pUOffset, pVOffset, pUWidth, pVHeight, pTextureWidth, pTextureHeight);
        if(changed) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
