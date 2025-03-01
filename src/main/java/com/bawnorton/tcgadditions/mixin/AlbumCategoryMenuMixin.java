package com.bawnorton.tcgadditions.mixin;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;

@Mixin(AlbumCategoryMenu.class)
public abstract class AlbumCategoryMenuMixin extends AbstractContainerMenu {
    protected AlbumCategoryMenuMixin(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    @Redirect(
            method = "quickMoveStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/Slot;setChanged()V"
            )
    )
    private void preventDupeBug(Slot instance, @Local(argsOnly = true) Player player, @Local(name = "slotsCount") int slotsCount, @Cancellable CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = instance.safeTake(1, 1, player);
        if(!moveItemStackTo(stack, slotsCount, slotsCount + 36, true)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
