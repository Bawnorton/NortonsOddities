package com.bawnorton.integrationfixes.mixin;

import com.bawnorton.integrationfixes.compat.itemobliterator.ItemObliteratorCompat;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {
    @Shadow private int shopItem;

    protected MerchantScreenMixin(MerchantMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @ModifyExpressionValue(
            method = {
                    "render",
                    "renderBg",
                    "mouseScrolled",
                    "mouseDragged",
                    "mouseClicked"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/MerchantMenu;getOffers()Lnet/minecraft/world/item/trading/MerchantOffers;"
            )
    )
    private MerchantOffers ignoreObliteratedOffers(MerchantOffers original) {
        return ItemObliteratorCompat.getFiltered(original);
    }

    @WrapOperation(
            method = {
                    "lambda$init$0",
                    "m_99173_"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;postButtonClick()V",
                    remap = true
            ),
            remap = false
    )
    private void accountForDisabled(MerchantScreen instance, Operation<Void> original) {
        MerchantOffers offers = menu.getOffers();
        List<Integer> disabledIndices = ItemObliteratorCompat.getDisabledIndices(offers);
        int disabledCount = 0;
        for (int disabledIndex : disabledIndices) {
            if (disabledIndex <= shopItem) {
                disabledCount++;
            }
        }
        shopItem += disabledCount;
        original.call(instance);
        shopItem -= disabledCount;
    }

    @Mixin(targets = "net.minecraft.client.gui.screens.inventory.MerchantScreen$TradeOfferButton")
    private static class TradeOfferButtonMixin {
        @ModifyExpressionValue(
                method = "renderToolTip",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/inventory/MerchantMenu;getOffers()Lnet/minecraft/world/item/trading/MerchantOffers;"
                )
        )
        private MerchantOffers ignoreObliteratedOffers(MerchantOffers original) {
            return ItemObliteratorCompat.getFiltered(original);
        }
    }
}
